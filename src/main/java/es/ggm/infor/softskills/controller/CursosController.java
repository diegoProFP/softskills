package es.ggm.infor.softskills.controller;

import es.ggm.infor.moodleintegration.dto.UsuarioMoodleDTO;
import es.ggm.infor.moodleintegration.exceptions.GeneralMoodleException;
import es.ggm.infor.softskills.dto.ErrorResponse;
import es.ggm.infor.softskills.model.Curso;
import es.ggm.infor.softskills.security.AuthenticatedUserService;
import es.ggm.infor.softskills.service.ICursoService;
import es.ggm.infor.softskills.service.ISoftSkillService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(MainController.BASE_PATH + "/cursos")
public class CursosController extends MainController {

    private final ICursoService cursoService;
    private final ISoftSkillService softSkillService;

    private final AuthenticatedUserService authenticatedUserService;


    public CursosController(ICursoService cursoService, ISoftSkillService softSkillService, AuthenticatedUserService authenticatedUserService) {
        this.cursoService = cursoService;
        this.softSkillService = softSkillService;
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

    @GetMapping("/{cursoId}/alumnos/{alumnoId}/soft-skills/{softSkillId}/muestras")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> getMuestrasCursoAlumnoSoftSkill(@PathVariable Long cursoId,
                                                             @PathVariable Long alumnoId,
                                                             @PathVariable Long softSkillId,
                                                             Authentication authentication) {
        try {
            UsuarioMoodleDTO usuario = authenticatedUserService.getAuthenticatedUser();
            return ResponseEntity.ok(softSkillService.obtenerMuestrasPorCursoAlumnoSoftSkill(
                    cursoId,
                    alumnoId,
                    softSkillId,
                    usuario.getUserid(),
                    isAdmin(authentication)
            ));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }

}
