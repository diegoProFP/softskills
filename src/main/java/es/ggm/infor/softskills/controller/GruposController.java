package es.ggm.infor.softskills.controller;

import es.ggm.infor.softskills.dto.AlumnoConTotalesDTO;
import es.ggm.infor.softskills.model.Grupo;
import es.ggm.infor.softskills.security.AuthenticatedUserService;
import es.ggm.infor.softskills.service.GrupoConsultaService;
import es.ggm.infor.softskills.service.GrupoResumenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(MainController.BASE_PATH + "/grupos")
@RequiredArgsConstructor
public class GruposController extends MainController {

    private final GrupoResumenService grupoResumenService;
    private final GrupoConsultaService grupoConsultaService;
    private final AuthenticatedUserService authenticatedUserService;

    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<Grupo>> getGrupos() {
        return ResponseEntity.ok(grupoConsultaService.getAllGrupos());
    }

    @GetMapping("/totales")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<AlumnoConTotalesDTO>> getTotalesGrupo(
            @RequestParam String nivel,
            @RequestParam String cicloFormativo,
            @RequestParam String grupo,
            @RequestParam String cursoEscolar
    ) throws Exception {
        String token = authenticatedUserService.getAuthenticatedToken();
        return ResponseEntity.ok(
                grupoResumenService.obtenerResumenGrupo(token, nivel, cicloFormativo, grupo, cursoEscolar)
        );
    }
}
