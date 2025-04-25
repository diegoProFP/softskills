package es.ggm.infor.softskills.security;

import es.ggm.infor.moodleintegration.client.IMoodleClient;
import es.ggm.infor.moodleintegration.client.MoodleClient;
import es.ggm.infor.moodleintegration.dto.MoodleLoginResponse;
import es.ggm.infor.moodleintegration.dto.UsuarioDTO;
import es.ggm.infor.moodleintegration.exceptions.GeneralMoodleException;
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

    public MoodleAuthenticationProvider(IMoodleClient moodleClient) {
        this.moodleClient = moodleClient;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        MoodleLoginResponse loginResponse = null;
            UsuarioDTO userInfo;
        try {
            loginResponse = moodleClient.login(username, password);
         //   loginResponse.token = "4b147711a37fd20806ea0a3b90f179b7";
            userInfo = moodleClient.getUserInfo(loginResponse.token);
            if (loginResponse.token == null) {
                throw new BadCredentialsException("Credenciales incorrectas en Moodle");
            }

            //TODO: pendiente de saber cómo podemos ponerle el rol de profesor o de alumno. Idea: consultar el userId en BD para ver si es profesor o alumno
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_TEACHER"));
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