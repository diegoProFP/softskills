package es.ggm.infor.softskills.security;

import es.ggm.infor.moodleintegration.dto.UsuarioMoodleDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class JwtFilter extends OncePerRequestFilter {


    private SecretKey secretKey;
    public JwtFilter(SecretKey secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }


        String token = authHeader.substring(7);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey) // el bean inyectado correctamente
                .build()
                .parseClaimsJws(token)
                .getBody();
        String username = claims.getSubject(); //
        List<String> roles = claims.get("roles", List.class); // si metiste los roles como lista
        Date exp = claims.getExpiration();
        if (exp.before(new Date())) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
            return;
        }
        if (username == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }
        if (roles == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            return;
        }


        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(username, null, authorities);

        // Aquí puedes agregar el objeto UsuarioDTO a los atributos de la solicitud
        UsuarioMoodleDTO userInfo = JwtUtils.convertClaimsToUserInfo(claims);
        userInfo.setMoodleToken(username);
        auth.setDetails(userInfo);

        SecurityContextHolder.getContext().setAuthentication(auth);

        request.setAttribute("username", username);
        filterChain.doFilter(request, response);
    }
}
