package es.ggm.infor.softskills.dto;

import es.ggm.infor.moodleintegration.dto.SiteInfoResponse;
import es.ggm.infor.moodleintegration.dto.UsuarioDTO;
import lombok.*;

@Data // Incluye getters, setters, toString, equals y hashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class LoginResponse {

    String token;
    UsuarioDTO datosUsuario;
    boolean exito;
    String mensaje;
}
