package es.ggm.infor.softskills.config;

import es.ggm.infor.softskills.dao.MotivosSoftSkillRepository;
import es.ggm.infor.softskills.dao.ProfesorRepository;
import es.ggm.infor.softskills.dao.SoftSkillRepository;
import es.ggm.infor.softskills.model.CodigoSoftSkill;
import es.ggm.infor.softskills.model.MotivosSoftSkill;
import es.ggm.infor.softskills.model.Profesor;
import es.ggm.infor.softskills.model.SoftSkill;
import es.ggm.infor.softskills.model.TipoMedicionSoftSkill;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        value = "soft-skills.bootstrap.reference-data.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class ReferenceDataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(ReferenceDataInitializer.class);

    private final SoftSkillRepository softSkillRepository;
    private final ProfesorRepository profesorRepository;
    private final MotivosSoftSkillRepository motivosSoftSkillRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        SoftSkill enfoque = ensureSoftSkill(
                CodigoSoftSkill.ENFOQUE_DISTRACCIONES,
                "Enfoque y concentracion",
                "Permite medir como maneja el alumno las distracciones",
                TipoMedicionSoftSkill.PENALIZACION_POR_TRAMOS,
                3
        );
        ensureMotivo(enfoque, "Mirando el móvil");
        ensureMotivo(enfoque, "Distraído haciendo tareas de otros módulos");
        ensureMotivo(enfoque, "Hablando con compañeros");

        ensureSoftSkill(
                CodigoSoftSkill.PUNTUALIDAD,
                "Puntualidad",
                "Mide la puntualidad del alumno",
                TipoMedicionSoftSkill.PENALIZACION_POR_TRAMOS,
                3
        );

        ensureSoftSkill(
                CodigoSoftSkill.PARTICIPACION,
                "Participacion",
                "Mide la participacion positiva del alumno",
                TipoMedicionSoftSkill.ACUMULACION_SATURADA,
                3
        );

        ensureProfesor(1445L, false);
        ensureProfesor(1446L, false);
    }

    private SoftSkill ensureSoftSkill(CodigoSoftSkill codigo,
                                      String nombre,
                                      String descripcion,
                                      TipoMedicionSoftSkill tipoMedicion,
                                      Integer prioridadRanking) {
        Optional<SoftSkill> existente = softSkillRepository.findAll().stream()
                .filter(skill -> codigo.equals(skill.getCodigo()))
                .findFirst();

        if (existente.isPresent()) {
            return existente.get();
        }

        SoftSkill nueva = SoftSkill.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .tipo(0)
                .codigo(codigo)
                .tipoMedicion(tipoMedicion)
                .prioridadRanking(prioridadRanking)
                .build();

        SoftSkill guardada = softSkillRepository.save(nueva);
        log.info("Soft skill de referencia creada: {} ({})", guardada.getNombre(), guardada.getCodigo());
        return guardada;
    }

    private void ensureProfesor(Long id, boolean administrador) {
        if (profesorRepository.existsById(id)) {
            return;
        }

        profesorRepository.save(Profesor.builder()
                .id(id)
                .administrador(administrador)
                .build());
        log.info("Profesor de referencia creado con id {}", id);
    }

    private void ensureMotivo(SoftSkill softSkill, String motivoTexto) {
        List<MotivosSoftSkill> motivos = softSkill.getListaMotivos();
        if (motivos == null) {
            motivos = new ArrayList<>();
            softSkill.setListaMotivos(motivos);
        }

        boolean existe = motivos.stream()
                .anyMatch(motivo -> motivoTexto.equalsIgnoreCase(motivo.getMotivo()));
        if (existe) {
            return;
        }

        AtomicLong siguienteId = new AtomicLong(motivosSoftSkillRepository.findMaxId().orElse(0L) + 1);
        MotivosSoftSkill nuevoMotivo = MotivosSoftSkill.builder()
                .id(siguienteId.getAndIncrement())
                .motivo(motivoTexto)
                .softSkill(softSkill)
                .build();

        motivos.add(nuevoMotivo);
        motivos.sort(Comparator.comparing(MotivosSoftSkill::getMotivo, String.CASE_INSENSITIVE_ORDER));
        softSkillRepository.save(softSkill);
        log.info("Motivo de referencia creado para la soft skill {}", softSkill.getCodigo());
    }
}
