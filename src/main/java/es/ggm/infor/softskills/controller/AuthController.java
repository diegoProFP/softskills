package es.ggm.infor.softskills.controller;


import es.ggm.infor.moodleintegration.client.MoodleClient;
import es.ggm.infor.moodleintegration.dto.MoodleLoginResponse;
import es.ggm.infor.moodleintegration.dto.SiteInfoResponse;
import es.ggm.infor.softskills.dto.LoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {


    private final MoodleClient moodleClient;

    public AuthController(MoodleClient moodleClient) {
        this.moodleClient = moodleClient;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        try {
            MoodleLoginResponse response =  moodleClient.login(request.username, request.password);
            SiteInfoResponse datosUsuario = moodleClient.getUserInfo(response.token);

            System.out.println("response = " + response);
            String token = response.token;

            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("user", datosUsuario);

            return ResponseEntity.ok(result);

        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Login incorrecto o Moodle no disponible");
        }
    }
}