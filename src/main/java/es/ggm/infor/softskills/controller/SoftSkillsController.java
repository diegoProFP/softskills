package es.ggm.infor.softskills.controller;

import es.ggm.infor.moodleintegration.dto.UsuarioMoodleDTO;
import es.ggm.infor.softskills.dto.AdminSoftSkillRequest;
import es.ggm.infor.softskills.dto.AdminSoftSkillResponse;
import es.ggm.infor.softskills.dto.MotivoSoftSkillDTO;
import es.ggm.infor.softskills.dto.MuestraRequest;
import es.ggm.infor.softskills.dto.SKResponse;
import es.ggm.infor.softskills.dto.SoftSkillAdminOptionsDTO;
import es.ggm.infor.softskills.model.CodigoSoftSkill;
import es.ggm.infor.softskills.model.MotivosSoftSkill;
import es.ggm.infor.softskills.model.SoftSkill;
import es.ggm.infor.softskills.model.TipoMedicionSoftSkill;
import es.ggm.infor.softskills.security.AuthenticatedUserService;
import es.ggm.infor.softskills.service.ISoftSkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping(MainController.BASE_PATH + "/softskills")
@RequiredArgsConstructor
public class SoftSkillsController {

    private static final Logger log = LoggerFactory.getLogger(SoftSkillsController.class);

    private final ISoftSkillService softSkillService;
    private final AuthenticatedUserService authenticatedUserService;

    @GetMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<SoftSkill>> getSoftSkills() {
        return ResponseEntity.ok(softSkillService.getAllSoftSkills());
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminSoftSkillResponse>> getSoftSkillsAdmin() {
        List<AdminSoftSkillResponse> softSkills = softSkillService.getAllSoftSkills().stream()
                .sorted(Comparator.comparing(SoftSkill::getId))
                .map(this::toAdminResponse)
                .toList();

        return ResponseEntity.ok(softSkills);
    }

    @GetMapping("/admin/opciones")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SoftSkillAdminOptionsDTO> getOpcionesAdministracion() {
        return ResponseEntity.ok(SoftSkillAdminOptionsDTO.builder()
                .tiposMedicion(Arrays.asList(TipoMedicionSoftSkill.values()))
                .codigos(Arrays.asList(CodigoSoftSkill.values()))
                .build());
    }

    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminSoftSkillResponse> actualizarSoftSkill(@PathVariable Long id,
                                                                      @Valid @RequestBody AdminSoftSkillRequest request) {
        return ResponseEntity.ok(toAdminResponse(softSkillService.actualizarSoftSkill(id, request)));
    }

    @GetMapping("/curso/{cursoId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<SoftSkill>> getSoftSkillsByCursoId(@PathVariable Long cursoId) {
        return ResponseEntity.ok(softSkillService.getSoftSkillsByCursoId(cursoId));
    }

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
            log.error("Error al grabar la muestra", e);
            SKResponse respuesta = SKResponse.builder()
                    .exito(false)
                    .mensaje("Se ha producido un error al grabar la muestra, consulte a su administrador.")
                    .build();
            return ResponseEntity.internalServerError().body(respuesta);

        }
    }

    private AdminSoftSkillResponse toAdminResponse(SoftSkill softSkill) {
        return AdminSoftSkillResponse.builder()
                .id(softSkill.getId())
                .nombre(softSkill.getNombre())
                .descripcion(softSkill.getDescripcion())
                .tipoMedicion(softSkill.getTipoMedicion())
                .codigo(softSkill.getCodigo())
                .listaMotivos(toMotivosResponse(softSkill.getListaMotivos()))
                .build();
    }

    private List<MotivoSoftSkillDTO> toMotivosResponse(List<MotivosSoftSkill> motivos) {
        if (motivos == null) {
            return List.of();
        }

        return motivos.stream()
                .sorted(Comparator.comparing(MotivosSoftSkill::getMotivo, String.CASE_INSENSITIVE_ORDER))
                .map(motivo -> MotivoSoftSkillDTO.builder()
                        .id(motivo.getId())
                        .motivo(motivo.getMotivo())
                        .build())
                .toList();
    }
}
