package es.ggm.infor.softskills.controller;


import es.ggm.infor.softskills.dto.AlumnoConTotalesDTO;
import es.ggm.infor.softskills.security.AuthenticatedUserService;
import es.ggm.infor.softskills.service.AlumnoDetalleService;
import es.ggm.infor.softskills.service.AlumnoResumenService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping(MainController.BASE_PATH + "/alumnos")
public class AlumnoController extends MainController{
    @Autowired
    private AlumnoResumenService resumenService;
    @Autowired private AlumnoDetalleService detalleService;
    @Autowired private AuthenticatedUserService authenticatedUserService;

    @GetMapping("/resumen")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    public ResponseEntity<?> getResumen(@RequestParam(required = false) Long idalumno,
                                        Authentication authentication) {
        if (idalumno == null) {
            return ResponseEntity.ok(resumenService.obtenerResumenGeneral());
        }

        try {
            boolean isTeacher = authentication.getAuthorities().stream()
                    .anyMatch(authority -> "ROLE_TEACHER".equals(authority.getAuthority()));
            boolean isStudent = authentication.getAuthorities().stream()
                    .anyMatch(authority -> "ROLE_STUDENT".equals(authority.getAuthority()));

            AlumnoConTotalesDTO resumen = detalleService.obtenerResumenAlumno(
                    idalumno,
                    authenticatedUserService.getAuthenticatedUser(),
                    authenticatedUserService.getAuthenticatedToken(),
                    isTeacher,
                    isStudent
            );
            return ResponseEntity.ok(resumen);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al obtener el resumen del alumno");
        }
    }

    @GetMapping("/{id}")
    public AlumnoConTotalesDTO getAlumnoConTotales(@PathVariable Long id) {
        return detalleService.obtenerDetalleAlumno(id);
    }

}
