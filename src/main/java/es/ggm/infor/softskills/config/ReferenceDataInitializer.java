package es.ggm.infor.softskills.config;

import es.ggm.infor.softskills.dao.MotivosSoftSkillRepository;
import es.ggm.infor.softskills.dao.ProfesorRepository;
import es.ggm.infor.softskills.dao.SoftSkillRepository;
import es.ggm.infor.softskills.model.CodigoSoftSkill;
import es.ggm.infor.softskills.model.MotivosSoftSkill;
import es.ggm.infor.softskills.model.NivelMuestraSoftSkill;
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

        SoftSkill autonomia = ensureSoftSkill(
                CodigoSoftSkill.AUTONOMIA,
                "Autonomia en el trabajo",
                "Mide la capacidad del alumno para avanzar, gestionar bloqueos, consultar recursos y pedir ayuda de forma preparada y a tiempo",
                TipoMedicionSoftSkill.EVIDENCIA_MIXTA,
                2
        );
        ensureMotivo(
                autonomia,
                "Avanza de forma independiente",
                "Avanza independiente",
                "El alumno progresa en la tarea sin supervision constante y toma decisiones razonables para continuar el trabajo.",
                1,
                NivelMuestraSoftSkill.LEVE
        );
        ensureMotivo(
                autonomia,
                "Resuelve bloqueo consultando recursos",
                "Resuelve con recursos",
                "El alumno se encuentra con una dificultad y la aborda consultando apuntes, documentacion, ejemplos o errores similares antes de pedir ayuda.",
                1,
                NivelMuestraSoftSkill.NORMAL
        );
        ensureMotivo(
                autonomia,
                "Formula pregunta preparada",
                "Pregunta preparada",
                "El alumno pide ayuda explicando que queria conseguir, que ha probado, que error o sintoma observa y que sospecha tiene.",
                1,
                NivelMuestraSoftSkill.NORMAL
        );
        ensureMotivo(
                autonomia,
                "Comunica bloqueo a tiempo",
                "Comunica bloqueo",
                "El alumno detecta que esta atascado y lo comunica antes de perder una cantidad relevante de tiempo sin avanzar.",
                1,
                NivelMuestraSoftSkill.LEVE
        );
        ensureMotivo(
                autonomia,
                "Aplica una pista y continua",
                "Aplica pista",
                "El alumno recibe una orientacion parcial, la interpreta y continua el trabajo sin necesitar una solucion paso a paso.",
                1,
                NivelMuestraSoftSkill.NORMAL
        );
        ensureMotivo(
                autonomia,
                "Divide el problema en pasos",
                "Divide en pasos",
                "El alumno transforma un problema amplio o ambiguo en pasos manejables, verificables y ordenados.",
                1,
                NivelMuestraSoftSkill.NORMAL
        );
        ensureMotivo(
                autonomia,
                "Pregunta sin intento previo",
                "Pregunta sin intento",
                "El alumno pide ayuda sin mostrar un intento razonable, sin haber revisado el enunciado o sin aportar informacion concreta del bloqueo.",
                -1,
                NivelMuestraSoftSkill.LEVE
        );
        ensureMotivo(
                autonomia,
                "No lee el enunciado o documentacion",
                "No lee enunciado",
                "El alumno pregunta o actua de forma incorrecta por no haber revisado instrucciones, materiales o documentacion disponible.",
                -1,
                NivelMuestraSoftSkill.NORMAL
        );
        ensureMotivo(
                autonomia,
                "Bloqueo pasivo prolongado",
                "Bloqueo pasivo",
                "El alumno permanece atascado durante un tiempo relevante sin probar alternativas, consultar recursos ni comunicar la dificultad.",
                -1,
                NivelMuestraSoftSkill.NORMAL
        );
        ensureMotivo(
                autonomia,
                "No comunica dificultades",
                "No comunica dificultad",
                "El alumno oculta o retrasa un bloqueo importante hasta que afecta al avance de la tarea o a la entrega.",
                -1,
                NivelMuestraSoftSkill.NORMAL
        );
        ensureMotivo(
                autonomia,
                "Dependencia reiterada del profesor",
                "Dependencia reiterada",
                "El alumno necesita confirmacion o instrucciones paso a paso de forma repetida en tareas que ya cuentan con pautas, ejemplos o explicaciones previas.",
                -1,
                NivelMuestraSoftSkill.SIGNIFICATIVA
        );
        ensureMotivo(
                autonomia,
                "Repite pregunta ya resuelta sin aplicar indicaciones",
                "Repite pregunta resuelta",
                "El alumno vuelve a plantear el mismo bloqueo sin haber aplicado la pista, explicacion o pauta recibida previamente.",
                -1,
                NivelMuestraSoftSkill.NORMAL
        );

        ensureProfesor(1445L, true);
        ensureProfesor(1446L, true);
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
        ensureMotivo(softSkill, motivoTexto, null, null, null, null);
    }

    private void ensureMotivo(SoftSkill softSkill,
                              String motivoTexto,
                              String descripcionCorta,
                              String descripcionLarga,
                              Integer valorPorDefecto,
                              NivelMuestraSoftSkill nivelPorDefecto) {
        List<MotivosSoftSkill> motivos = softSkill.getListaMotivos();
        if (motivos == null) {
            motivos = new ArrayList<>();
            softSkill.setListaMotivos(motivos);
        }

        Optional<MotivosSoftSkill> existente = motivos.stream()
                .filter(motivo -> motivoTexto.equalsIgnoreCase(motivo.getMotivo()))
                .findFirst();
        if (existente.isPresent()) {
            MotivosSoftSkill motivo = existente.get();
            motivo.setDescripcionCorta(descripcionCorta);
            motivo.setDescripcionLarga(descripcionLarga);
            motivo.setValorPorDefecto(valorPorDefecto);
            motivo.setNivelPorDefecto(nivelPorDefecto);
            softSkillRepository.save(softSkill);
            return;
        }

        AtomicLong siguienteId = new AtomicLong(motivosSoftSkillRepository.findMaxId().orElse(0L) + 1);
        MotivosSoftSkill nuevoMotivo = MotivosSoftSkill.builder()
                .id(siguienteId.getAndIncrement())
                .motivo(motivoTexto)
                .descripcionCorta(descripcionCorta)
                .descripcionLarga(descripcionLarga)
                .valorPorDefecto(valorPorDefecto)
                .nivelPorDefecto(nivelPorDefecto)
                .softSkill(softSkill)
                .build();

        motivos.add(nuevoMotivo);
        motivos.sort(Comparator.comparing(MotivosSoftSkill::getMotivo, String.CASE_INSENSITIVE_ORDER));
        softSkillRepository.save(softSkill);
        log.info("Motivo de referencia creado para la soft skill {}", softSkill.getCodigo());
    }
}
