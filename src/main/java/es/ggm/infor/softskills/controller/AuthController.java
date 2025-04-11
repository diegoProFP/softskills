package es.ggm.infor.softskills.controller;


import es.ggm.infor.moodleintegration.client.MoodleClient;
import es.ggm.infor.moodleintegration.dto.SiteInfoResponse;
import es.ggm.infor.moodleintegration.dto.UsuarioDTO;
import es.ggm.infor.softskills.dto.LoginRequest;
import es.ggm.infor.softskills.security.JwtUtils;
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
@RequestMapping("/api")
public class AuthController {


    private final MoodleClient moodleClient;

    @Autowired
    private SecretKey secretKey;

    private final AuthenticationManager authenticationManager;

    public AuthController(MoodleClient moodleClient, AuthenticationManager authenticationManager) {
        this.moodleClient = moodleClient;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            String moodleToken = authentication.getName();
            UsuarioDTO userInfo = moodleClient.getUserInfo(authentication.getName());

            String token = JwtUtils.generateToken(authentication, userInfo, secretKey);
            return "Bearer " + token;
        } catch (AuthenticationException e) {
            return "Error en login: " + e.getMessage();
        }
    }
}