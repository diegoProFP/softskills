package es.ggm.infor.softskills.security;

import es.ggm.infor.moodleintegration.dto.UsuarioDTO;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.Authentication;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JwtUtils {

    public static String generateToken(Authentication authentication, UsuarioDTO userInfo, SecretKey secretKey) {
        List<String> roles = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toList());
        Map<String, Object> userClaims = convertUserInfoToClaims(userInfo);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("roles", roles)
                .addClaims(userClaims) // Añade los claims del UsuarioDTO
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(secretKey)
                .compact();
    }

    private static Map<String, Object> convertUserInfoToClaims(UsuarioDTO userInfo) {
        return Map.of(
                "userid", userInfo.getUserid(),
                "username", userInfo.getUsername(),
                "firstname", userInfo.getFirstname(),
                "lastname", userInfo.getLastname(),
                "fullname", userInfo.getFullname(),
                "lang", userInfo.getLang(),
                "sitename", userInfo.getSitename(),
                "siteurl", userInfo.getSiteurl(),
                "userPictureUrl", userInfo.getUserPictureUrl()
        );
    }
}
