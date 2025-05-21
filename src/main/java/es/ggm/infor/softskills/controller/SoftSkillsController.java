package es.ggm.infor.softskills.controller;

import es.ggm.infor.moodleintegration.dto.UsuarioMoodleDTO;
import es.ggm.infor.softskills.dto.MuestraRequest;
import es.ggm.infor.softskills.dto.SKResponse;
import es.ggm.infor.softskills.security.AuthenticatedUserService;
import es.ggm.infor.softskills.service.ISoftSkillService;
import es.ggm.infor.softskills.service.SoftSkillService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(MainController.BASE_PATH + "/softskills")
@RequiredArgsConstructor
public class SoftSkillsController {

    private static final Logger log = LoggerFactory.getLogger(SoftSkillsController.class);

    private final ISoftSkillService softSkillService;
    private final AuthenticatedUserService authenticatedUserService;

    @PostMapping("/muestra")
    public ResponseEntity<SKResponse> registrarMuestra(@RequestBody MuestraRequest request) {


        try {
            UsuarioMoodleDTO usuario = authenticatedUserService.getAuthenticatedUser();
            String token = authenticatedUserService.getAuthenticatedToken();

            request.setProfesorId(usuario.getUserid());
            log.info("Recibida solicitud para insertar muestra: {}", request);
            softSkillService.insertarMuestra(request);
            SKResponse respuesta = SKResponse.builder().exito(true).build();
            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            log.error("Error al grabar la muestra: {}", e.getMessage());
            SKResponse respuesta = SKResponse.builder().exito(false).mensaje("Error al grabar la muestra: " + e.getMessage()).build();
            return ResponseEntity.internalServerError().body(respuesta);

        }
    }
}