package es.ggm.infor.softskills.security;

import es.ggm.infor.moodleintegration.client.IMoodleClient;
import es.ggm.infor.moodleintegration.dto.MoodleLoginResponse;
import es.ggm.infor.moodleintegration.dto.UsuarioMoodleDTO;
import es.ggm.infor.moodleintegration.exceptions.GeneralMoodleException;
import es.ggm.infor.softskills.service.ProfesorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MoodleAuthenticationProvider implements AuthenticationProvider {

    private final IMoodleClient moodleClient;

    private final ProfesorService profesorService;

    @Value("${soft-skills.login-manual}")
    private boolean loginManual;

    @Value("${soft-skills.token-manual}")
    private String tokenManual;

    public MoodleAuthenticationProvider(IMoodleClient moodleClient, ProfesorService profesorService) {
        this.moodleClient = moodleClient;
        this.profesorService = profesorService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        MoodleLoginResponse loginResponse = null;
            UsuarioMoodleDTO userInfo;
        try {
            if(this.loginManual){
                // Si el login es manual, se devuelve el token manual
                loginResponse = new MoodleLoginResponse();
                loginResponse.token = tokenManual;
            } else {
                // Si el login no es manual, se hace la llamada a Moodle
                loginResponse = moodleClient.login(username, password);
            }

            userInfo = moodleClient.getUserInfo(loginResponse.token);
            if (loginResponse.token == null) {
                throw new BadCredentialsException("Credenciales incorrectas en Moodle: " + username + ", " + password);
            }

            // Consultar si el userId existe en la base de datos como profesor
            boolean isProfesor = profesorService.getProfesorById(userInfo.getUserid()) != null;

            List<SimpleGrantedAuthority> authorities = isProfesor
                    ? List.of(new SimpleGrantedAuthority("ROLE_TEACHER"))
                    : List.of(new SimpleGrantedAuthority("ROLE_STUDENT"));

            UsernamePasswordAuthenticationToken tokenAuth = new UsernamePasswordAuthenticationToken(loginResponse.token, null, authorities);

            tokenAuth.setDetails(userInfo);

            return tokenAuth;
        } catch (GeneralMoodleException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }


}