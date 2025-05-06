package es.ggm.infor.softskills.controller;


import es.ggm.infor.moodleintegration.client.IMoodleClient;
import es.ggm.infor.moodleintegration.client.MoodleClient;
import es.ggm.infor.moodleintegration.dto.SiteInfoResponse;
import es.ggm.infor.moodleintegration.dto.UsuarioMoodleDTO;
import es.ggm.infor.softskills.dto.LoginRequest;
import es.ggm.infor.softskills.dto.LoginResponse;
import es.ggm.infor.softskills.model.Alumno;
import es.ggm.infor.softskills.security.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;


@RestController
@RequestMapping(MainController.BASE_PATH + "/auth")
public class AuthController extends MainController{

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    private final IMoodleClient moodleClient;

    @Autowired
    private SecretKey secretKey;

    private final AuthenticationManager authenticationManager;

    public AuthController(IMoodleClient moodleClient, AuthenticationManager authenticationManager) {
        this.moodleClient = moodleClient;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            String moodleToken = authentication.getName();
            UsuarioMoodleDTO userInfo = (UsuarioMoodleDTO) authentication.getDetails();

            String token = JwtUtils.generateToken(authentication, userInfo, secretKey);


            LoginResponse respuestaLogin = LoginResponse.builder().token(token).datosUsuario(userInfo).exito(true).build();

            logger.info("Usuario logado: " + userInfo.getFullname() + "| Token: " + token);
            return ResponseEntity.ok(respuestaLogin);
        } catch (AuthenticationException e) {
            LoginResponse respuestaLogin = LoginResponse.builder().exito(false).mensaje("Error en login: " + e.getMessage()).build();

            logger.error("Error en login: " + e.getMessage());
            return ResponseEntity.badRequest().body(respuestaLogin);
        }
    }
}