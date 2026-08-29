package es.ggm.infor.softskills.controller;

import es.ggm.infor.softskills.dto.AdminGrupoAcademicoRequest;
import es.ggm.infor.softskills.dto.AdminGrupoAcademicoResponse;
import es.ggm.infor.softskills.dto.AlumnoConTotalesDTO;
import es.ggm.infor.softskills.model.Grupo;
import es.ggm.infor.softskills.security.AuthenticatedUserService;
import es.ggm.infor.softskills.service.GrupoConsultaService;
import es.ggm.infor.softskills.service.GrupoResumenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
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

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminGrupoAcademicoResponse>> getGruposAdmin() {
        List<AdminGrupoAcademicoResponse> grupos = grupoConsultaService.getAllGrupos().stream()
                .sorted(Comparator
                        .comparing(Grupo::getCursoEscolar)
                        .thenComparing(Grupo::getNivel)
                        .thenComparing(Grupo::getCicloFormativo)
                        .thenComparing(Grupo::getGrupo))
                .map(this::toAdminResponse)
                .toList();

        return ResponseEntity.ok(grupos);
    }

    @PutMapping("/admin/{id}/curso-moodle-grupo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminGrupoAcademicoResponse> actualizarCursoMoodleGrupo(
            @PathVariable Long id,
            @Valid @RequestBody AdminGrupoAcademicoRequest request
    ) {
        return ResponseEntity.ok(toAdminResponse(grupoConsultaService.actualizarCursoMoodleGrupo(id, request)));
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

    private AdminGrupoAcademicoResponse toAdminResponse(Grupo grupo) {
        return AdminGrupoAcademicoResponse.builder()
                .id(grupo.getId())
                .nivel(grupo.getNivel())
                .cicloFormativo(grupo.getCicloFormativo())
                .grupo(grupo.getGrupo())
                .cursoEscolar(grupo.getCursoEscolar())
                .cursoMoodleGrupoId(grupo.getCursoMoodleGrupoId())
                .build();
    }
}
