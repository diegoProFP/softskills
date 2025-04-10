package es.ggm.infor.softskills.dto;

import es.ggm.infor.moodleintegration.dto.SiteInfoResponse;
import lombok.*;

@Data // Incluye getters, setters, toString, equals y hashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class LoginResponse {

    String token;
    SiteInfoResponse datosUsuario;

    boolean exito;
    String mensaje;
}
