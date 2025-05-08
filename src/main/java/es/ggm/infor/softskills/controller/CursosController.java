package es.ggm.infor.softskills.controller;

import es.ggm.infor.moodleintegration.client.MoodleClient;
import es.ggm.infor.moodleintegration.dto.CursoMoodleDTO;
import es.ggm.infor.moodleintegration.dto.UsuarioMoodleDTO;
import es.ggm.infor.moodleintegration.exceptions.GeneralMoodleException;
import es.ggm.infor.softskills.model.Curso;
import es.ggm.infor.softskills.security.AuthenticatedUserService;
import es.ggm.infor.softskills.service.CursoService;
import es.ggm.infor.softskills.service.ICursoService;
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

    private final ICursoService cursoService;

    private final AuthenticatedUserService authenticatedUserService;


    public CursosController(MoodleClient moodleClient, ICursoService cursoService, AuthenticatedUserService authenticatedUserService) {
        this.moodleClient = moodleClient;
        this.cursoService = cursoService;
        this.authenticatedUserService = authenticatedUserService;
    }

    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> getCursosUsuario() {
       
        List<CursoMoodleDTO> cursos = null;
        List<Curso> cursosDelProfesor = null;
        try {
            UsuarioMoodleDTO usuario = authenticatedUserService.getAuthenticatedUser();
            String token = authenticatedUserService.getAuthenticatedToken();

            cursosDelProfesor = cursoService.getCursosDelProfesor(token, usuario.getUserid());

            return ResponseEntity.ok(cursosDelProfesor);
        } catch (Exception e) {
            logger.error("Error al obtener los cursos del usuario: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Error al obtener los cursos: " + e.getMessage());

        }

    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> getCurso(@PathVariable String id) {


        try {
            // Obtener usuario autenticado y token
            UsuarioMoodleDTO usuario = authenticatedUserService.getAuthenticatedUser();
            String token = authenticatedUserService.getAuthenticatedToken();

            // Llamar al servicio para registrar el curso
            Curso recuperado = cursoService.obtenerCursoConAlumnos(token, Long.parseLong(id));

            return ResponseEntity.ok(recuperado);
        } catch (GeneralMoodleException e) {
            logger.error("Error al registrar el curso con ID {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().body("Error al registrar el curso: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al registrar el curso con ID {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().body("Error inesperado: " + e.getMessage());
        }
    }


    @GetMapping("/{id}/registrar")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> registrarCurso(@PathVariable Long id) {
        try {
            // Obtener usuario autenticado y token
            UsuarioMoodleDTO usuario = authenticatedUserService.getAuthenticatedUser();
            String token = authenticatedUserService.getAuthenticatedToken();

            // Llamar al servicio para registrar el curso
            cursoService.registrarCurso(token, id, usuario.getUserid());

            return ResponseEntity.ok("Curso registrado correctamente");
        } catch (GeneralMoodleException e) {
            logger.error("Error al registrar el curso con ID {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().body("Error al registrar el curso: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al registrar el curso con ID {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().body("Error inesperado: " + e.getMessage());
        }
    }

}
