package es.ggm.infor.softskills.controller;


import es.ggm.infor.moodleintegration.dto.UsuarioMoodleDTO;
import es.ggm.infor.softskills.dto.LoginRequest;
import es.ggm.infor.softskills.dto.LoginResponse;
import es.ggm.infor.softskills.security.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.util.List;


@RestController
@RequestMapping(MainController.BASE_PATH + "/auth")
public class AuthController extends MainController{

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    @Autowired
    private SecretKey secretKey;

    private final AuthenticationManager authenticationManager;

    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UsuarioMoodleDTO userInfo = (UsuarioMoodleDTO) authentication.getDetails();

            String token = JwtUtils.generateToken(authentication, userInfo, secretKey);

            List<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            LoginResponse respuestaLogin = LoginResponse.builder().token(token).datosUsuario(userInfo).roles(roles).exito(true).build();

            logger.info("Usuario logado: {} ({})", userInfo.getFullname(), userInfo.getUserid());
            return ResponseEntity.ok(respuestaLogin);
        } catch (AuthenticationException e) {
            return construirRespuestaLoginFallido(request, e);
        }
    }

    private ResponseEntity<LoginResponse> construirRespuestaLoginFallido(LoginRequest request, AuthenticationException e) {
        if (e instanceof AuthenticationServiceException) {
            logger.warn("Login no completado para usuario '{}': servicio de autenticacion no disponible",
                    request.getUsername());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(LoginResponse.builder()
                            .exito(false)
                            .codigoError("MOODLE_NO_DISPONIBLE")
                            .mensaje("No se ha podido validar el login con Moodle. Inténtalo de nuevo más tarde.")
                            .build());
        }

        if (e instanceof BadCredentialsException) {
            logger.warn("Intento de login fallido para usuario '{}': credenciales invalidas", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(LoginResponse.builder()
                            .exito(false)
                            .codigoError("CREDENCIALES_INVALIDAS")
                            .mensaje("Usuario o contraseña incorrectos.")
                            .build());
        }

        logger.warn("Login rechazado para usuario '{}': {}", request.getUsername(), e.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(LoginResponse.builder()
                        .exito(false)
                        .codigoError("LOGIN_RECHAZADO")
                        .mensaje("No se ha podido iniciar sesión con esas credenciales.")
                        .build());
    }
}
