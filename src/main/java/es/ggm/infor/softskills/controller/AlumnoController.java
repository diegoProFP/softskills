package es.ggm.infor.softskills.controller;


import es.ggm.infor.moodleintegration.exceptions.GeneralMoodleException;
import es.ggm.infor.softskills.dto.AlumnoConTotalesDTO;
import es.ggm.infor.softskills.security.AuthenticatedUserService;
import es.ggm.infor.softskills.service.AlumnoDetalleService;
import es.ggm.infor.softskills.service.AlumnoResumenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public List<AlumnoConTotalesDTO> getResumenGeneral() {
        return resumenService.obtenerResumenGeneral();
    }

    @GetMapping("/{id}")
    public AlumnoConTotalesDTO getAlumnoConTotales(@PathVariable Long id) {
        return detalleService.obtenerDetalleAlumno(id);
    }

    @GetMapping("/{alumnoId}/resumen")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    public ResponseEntity<?> getResumenAlumno(@PathVariable Long alumnoId, Authentication authentication) throws GeneralMoodleException {
        boolean isTeacher = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_TEACHER".equals(authority.getAuthority()));
        boolean isStudent = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_STUDENT".equals(authority.getAuthority()));

        AlumnoConTotalesDTO resumen = detalleService.obtenerResumenAlumno(
                alumnoId,
                authenticatedUserService.getAuthenticatedUser(),
                authenticatedUserService.getAuthenticatedToken(),
                isTeacher,
                isStudent
        );
        return ResponseEntity.ok(resumen);
    }
}
