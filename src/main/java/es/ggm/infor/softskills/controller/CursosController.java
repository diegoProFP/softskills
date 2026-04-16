package es.ggm.infor.softskills.controller;

import es.ggm.infor.moodleintegration.dto.UsuarioMoodleDTO;
import es.ggm.infor.moodleintegration.exceptions.GeneralMoodleException;
import es.ggm.infor.softskills.model.Curso;
import es.ggm.infor.softskills.security.AuthenticatedUserService;
import es.ggm.infor.softskills.service.ICursoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(MainController.BASE_PATH + "/cursos")
public class CursosController extends MainController {

    private final ICursoService cursoService;

    private final AuthenticatedUserService authenticatedUserService;


    public CursosController(ICursoService cursoService, AuthenticatedUserService authenticatedUserService) {
        this.cursoService = cursoService;
        this.authenticatedUserService = authenticatedUserService;
    }

    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> getCursosUsuario() {
        UsuarioMoodleDTO usuario = authenticatedUserService.getAuthenticatedUser();
        String token = authenticatedUserService.getAuthenticatedToken();
        List<Curso> cursosDelProfesor = cursoService.getCursosDelProfesor(token, usuario.getUserid());
        return ResponseEntity.ok(cursosDelProfesor);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> getCurso(@PathVariable String id) throws GeneralMoodleException {
        UsuarioMoodleDTO usuario = authenticatedUserService.getAuthenticatedUser();
        String token = authenticatedUserService.getAuthenticatedToken();
        Curso recuperado = cursoService.obtenerCursoConAlumnos(token, Long.parseLong(id));
        return ResponseEntity.ok(recuperado);
    }


    @PostMapping("/{id}/registrar")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<?> registrarCurso(@PathVariable Long id) throws GeneralMoodleException {
        UsuarioMoodleDTO usuario = authenticatedUserService.getAuthenticatedUser();
        String token = authenticatedUserService.getAuthenticatedToken();
        cursoService.registrarCurso(token, id, usuario.getUserid());
        return ResponseEntity.ok("Curso registrado correctamente");
    }

}
