package es.ggm.infor.softskills.controller;

import es.ggm.infor.moodleintegration.client.MoodleClient;
import es.ggm.infor.moodleintegration.dto.CursoDTO;
import es.ggm.infor.moodleintegration.dto.UsuarioDTO;
import es.ggm.infor.moodleintegration.exceptions.GeneralMoodleException;
import org.springframework.http.HttpStatus;
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
public class CursosController extends MainController{


    private final MoodleClient moodleClient;
    public CursosController(MoodleClient moodleClient) {
        this.moodleClient = moodleClient;
    }
    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> getCursosUsuario() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autorizado");
        }

        String token = (String) auth.getPrincipal(); // el token guardado en el login
        UsuarioDTO usuario = (UsuarioDTO) auth.getDetails(); // el objeto que contiene el userid

        List<CursoDTO> cursos = null;
        try {
            cursos = moodleClient.getCursos(token, usuario.getUserid());
        } catch (GeneralMoodleException e) {
            return ResponseEntity.internalServerError().body("Error al obtener los cursos: " + e.getMessage());
        }

        return ResponseEntity.ok(cursos);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String getAlumnos(@PathVariable String id) {


       Authentication auth = SecurityContextHolder.getContext().getAuthentication();

       

        return "Lista de alumnos del curso  " + id;
    }
}
