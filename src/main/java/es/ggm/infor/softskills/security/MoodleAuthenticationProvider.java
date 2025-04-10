package es.ggm.infor.softskills.security;

import es.ggm.infor.moodleintegration.client.MoodleClient;
import es.ggm.infor.moodleintegration.dto.MoodleLoginResponse;
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

    private final MoodleClient moodleClient;

    public MoodleAuthenticationProvider(MoodleClient moodleClient) {
        this.moodleClient = moodleClient;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // Aquí llamas a tu API externa (por ejemplo, RESTTemplate, WebClient...)

        MoodleLoginResponse loginResponse = moodleClient.login(username, password);

        if(loginResponse == null) {
            throw new BadCredentialsException("Credenciales incorrectas en Moodle");
        }


        // Si es válido, creas el Authentication con roles, etc.
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        return new UsernamePasswordAuthenticationToken(loginResponse.token, null, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private boolean moodleApiValida(String user, String pass) {

        MoodleLoginResponse loginResponse = moodleClient.login(user, pass);
        // Aquí va tu llamada real a Moodle (esto es solo un mock)
        return "admin".equals(user) && "admin".equals(pass);
    }
}