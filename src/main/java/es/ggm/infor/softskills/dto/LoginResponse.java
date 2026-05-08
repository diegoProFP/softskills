package es.ggm.infor.softskills.dto;

import es.ggm.infor.moodleintegration.dto.UsuarioMoodleDTO;
import lombok.*;

import java.util.List;

@Data // Incluye getters, setters, toString, equals y hashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class LoginResponse {

    String token;
    UsuarioMoodleDTO datosUsuario;
    List<String> roles;
    boolean exito;
    String mensaje;
    String codigoError;


}
