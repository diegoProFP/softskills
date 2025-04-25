package es.ggm.infor.softskills.security;

import es.ggm.infor.moodleintegration.dto.UsuarioDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedUserService {

    public UsuarioDTO getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("No hay un usuario autenticado");
        }

        return (UsuarioDTO) auth.getDetails(); // Asegúrate de que los detalles sean un UsuarioDTO
    }

    public String getAuthenticatedToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("No hay un usuario autenticado");
        }

        return getAuthenticatedUser().getMoodleToken(); // El token guardado en el login
    }
}