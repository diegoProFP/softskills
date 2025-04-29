package es.ggm.infor.softskills.controller;

import es.ggm.infor.moodleintegration.client.MoodleClient;
import es.ggm.infor.moodleintegration.dto.CursoMoodleDTO;
import es.ggm.infor.moodleintegration.dto.UsuarioMoodleDTO;
import es.ggm.infor.moodleintegration.exceptions.GeneralMoodleException;
import es.ggm.infor.softskills.security.AuthenticatedUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(MainController.BASE_PATH + "/cursos")
public class CursosController extends MainController {

    private static final Logger logger = LoggerFactory.getLogger(CursosController.class);


    private final MoodleClient moodleClient;

    private final AuthenticatedUserService authenticatedUserService;


    public CursosController(MoodleClient moodleClient, AuthenticatedUserService authenticatedUserService) {
        this.moodleClient = moodleClient;
        this.authenticatedUserService = authenticatedUserService;
    }

    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> getCursosUsuario() {
       
        List<CursoMoodleDTO> cursos = null;
        try {
            UsuarioMoodleDTO usuario = authenticatedUserService.getAuthenticatedUser();
            String token = authenticatedUserService.getAuthenticatedToken();

            cursos = moodleClient.getCursos(token, usuario.getUserid());
            return ResponseEntity.ok(cursos);
        } catch (GeneralMoodleException e) {
            logger.error("Error al obtener los cursos del usuario: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Error al obtener los cursos: " + e.getMessage());

        }

    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String getAlumnos(@PathVariable String id) {


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();


        return "Lista de alumnos del curso  " + id;
    }
}
