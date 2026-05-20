package es.ggm.infor.softskills.service;

import es.ggm.infor.softskills.dao.TotalSoftSkillPorAlumnoCursoRepository;
import es.ggm.infor.softskills.dao.TotalSoftSkillPorAlumnoGrupoRepository;
import es.ggm.infor.softskills.dao.TotalSoftSkillRepository;
import es.ggm.infor.softskills.dao.MuestraSoftSkillRepository;
import es.ggm.infor.softskills.model.Alumno;
import es.ggm.infor.softskills.model.Curso;
import es.ggm.infor.softskills.model.Grupo;
import es.ggm.infor.softskills.model.MuestraSoftSkill;
import es.ggm.infor.softskills.model.SoftSkill;
import es.ggm.infor.softskills.model.SoftSkillTotalizable;
import es.ggm.infor.softskills.model.TotalSoftSkillPorAlumno;
import es.ggm.infor.softskills.model.TotalSoftSkillPorAlumnoCurso;
import es.ggm.infor.softskills.model.TotalSoftSkillPorAlumnoGrupo;
import es.ggm.infor.softskills.service.strategy.SoftSkillTotalStrategy;
import es.ggm.infor.softskills.service.strategy.SoftSkillTotalStrategyResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SoftSkillTotalService {

    // El total global del alumno no replica muestra a muestra lo que ocurre en cada curso.
    // Se recalcula a partir del agregado intermedio mas cercano al seguimiento academico:
    // preferentemente por grupo y, como compatibilidad para datos antiguos, por curso.
    private static final BigDecimal GLOBAL_SMOOTHING_FACTOR = new BigDecimal("0.25");
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal MAX_SCORE = new BigDecimal("10.00");

    private final TotalSoftSkillRepository totalSoftSkillRepository;
    private final TotalSoftSkillPorAlumnoCursoRepository totalSoftSkillPorAlumnoCursoRepository;
    private final TotalSoftSkillPorAlumnoGrupoRepository totalSoftSkillPorAlumnoGrupoRepository;
    private final SoftSkillTotalStrategyResolver strategyResolver;
    private final GrupoService grupoService;
    private final MuestraSoftSkillRepository muestraSoftSkillRepository;

    @Transactional
    public void aplicarNuevaMuestra(MuestraSoftSkill muestra) {
        Alumno alumno = muestra.getAlumno();
        Curso curso = muestra.getCurso();
        SoftSkill softSkill = muestra.getSoftSkill();
        SoftSkillTotalStrategy strategy = strategyResolver.resolve(softSkill);
        Grupo grupo = grupoService.resolverGrupoDesdeCurso(curso);

        actualizarTotalPorCurso(alumno, curso, softSkill, muestra, strategy);

        if (grupo != null) {
            actualizarTotalPorGrupo(alumno, grupo, softSkill);
        }

        actualizarTotalGlobal(alumno, softSkill, grupo != null);
    }

    @Transactional
    public Optional<TotalSoftSkillPorAlumnoCurso> recalcularTrasCambio(MuestraSoftSkill muestraReferencia) {
        Alumno alumno = muestraReferencia.getAlumno();
        Curso curso = muestraReferencia.getCurso();
        SoftSkill softSkill = muestraReferencia.getSoftSkill();
        SoftSkillTotalStrategy strategy = strategyResolver.resolve(softSkill);
        Grupo grupo = grupoService.resolverGrupoDesdeCurso(curso);

        Optional<TotalSoftSkillPorAlumnoCurso> totalCurso = recalcularTotalPorCurso(alumno, curso, softSkill, strategy);

        if (grupo != null) {
            actualizarTotalPorGrupo(alumno, grupo, softSkill);
        }

        actualizarTotalGlobal(alumno, softSkill, grupo != null);
        return totalCurso;
    }

    private Optional<TotalSoftSkillPorAlumnoCurso> recalcularTotalPorCurso(Alumno alumno,
                                                                           Curso curso,
                                                                           SoftSkill softSkill,
                                                                           SoftSkillTotalStrategy strategy) {
        List<MuestraSoftSkill> muestras = muestraSoftSkillRepository
                .findByCurso_IdAndAlumno_IdAndSoftSkill_IdOrderByFechaAscIdAsc(curso.getId(), alumno.getId(), softSkill.getId());

        Optional<TotalSoftSkillPorAlumnoCurso> totalExistente = totalSoftSkillPorAlumnoCursoRepository
                .findByAlumnoAndCursoAndSoftSkill(alumno, curso, softSkill);

        if (muestras.isEmpty()) {
            totalExistente.ifPresent(totalSoftSkillPorAlumnoCursoRepository::delete);
            return Optional.empty();
        }

        TotalSoftSkillPorAlumnoCurso total = totalExistente.orElseGet(() -> TotalSoftSkillPorAlumnoCurso.builder()
                .alumno(alumno)
                .curso(curso)
                .softSkill(softSkill)
                .build());

        total.setPuntuacionTotal(null);
        total.setNumMuestras(null);
        total.setNumIncidencias(null);
        total.setEvidenciaAcumulada(null);

        for (MuestraSoftSkill muestra : muestras) {
            strategy.aplicarAlta(total, muestra);
        }

        return Optional.of(totalSoftSkillPorAlumnoCursoRepository.save(total));
    }

    private void actualizarTotalPorCurso(Alumno alumno, Curso curso, SoftSkill softSkill,
                                         MuestraSoftSkill muestra, SoftSkillTotalStrategy strategy) {
        Optional<TotalSoftSkillPorAlumnoCurso> totalExistente = totalSoftSkillPorAlumnoCursoRepository
                .findByAlumnoAndCursoAndSoftSkill(alumno, curso, softSkill);

        TotalSoftSkillPorAlumnoCurso total = totalExistente.orElseGet(() -> TotalSoftSkillPorAlumnoCurso.builder()
                .alumno(alumno)
                .curso(curso)
                .softSkill(softSkill)
                .build());

        strategy.aplicarAlta(total, muestra);
        totalSoftSkillPorAlumnoCursoRepository.save(total);
    }

    private void actualizarTotalPorGrupo(Alumno alumno, Grupo grupo, SoftSkill softSkill) {
        List<TotalSoftSkillPorAlumnoCurso> totalesPorCursoDelGrupo = totalSoftSkillPorAlumnoCursoRepository
                .findByAlumnoAndCurso_GrupoAcademicoAndSoftSkill(alumno, grupo, softSkill);

        Optional<TotalSoftSkillPorAlumnoGrupo> totalExistente = totalSoftSkillPorAlumnoGrupoRepository
                .findByAlumnoAndGrupoAndSoftSkill(alumno, grupo, softSkill);

        if (totalesPorCursoDelGrupo.isEmpty()) {
            totalExistente.ifPresent(totalSoftSkillPorAlumnoGrupoRepository::delete);
            return;
        }

        TotalSoftSkillPorAlumnoGrupo total = totalExistente.orElseGet(() -> TotalSoftSkillPorAlumnoGrupo.builder()
                .alumno(alumno)
                .grupo(grupo)
                .softSkill(softSkill)
                .build());

        recalcularAgregado(total, totalesPorCursoDelGrupo);
        totalSoftSkillPorAlumnoGrupoRepository.save(total);
    }

    private void actualizarTotalGlobal(Alumno alumno, SoftSkill softSkill, boolean usarGrupoComoFuente) {
        List<? extends SoftSkillTotalizable> totalesFuente = usarGrupoComoFuente
                ? totalSoftSkillPorAlumnoGrupoRepository.findByAlumnoAndSoftSkill(alumno, softSkill)
                : totalSoftSkillPorAlumnoCursoRepository.findByAlumnoAndSoftSkill(alumno, softSkill);

        Optional<TotalSoftSkillPorAlumno> totalExistente = totalSoftSkillRepository.findByAlumnoAndSoftSkill(alumno, softSkill);

        if (totalesFuente.isEmpty()) {
            totalExistente.ifPresent(totalSoftSkillRepository::delete);
            return;
        }

        TotalSoftSkillPorAlumno total = totalExistente.orElseGet(() -> TotalSoftSkillPorAlumno.builder()
                .alumno(alumno)
                .softSkill(softSkill)
                .build());

        recalcularTotalGlobal(total, totalesFuente);
        totalSoftSkillRepository.save(total);
    }

    private void recalcularAgregado(SoftSkillTotalizable totalDestino,
                                    List<? extends SoftSkillTotalizable> totalesFuente) {
        BigDecimal puntuacionObjetivo = calcularObjetivoGlobal(totalesFuente);
        totalDestino.setPuntuacionTotal(acotarPuntuacion(puntuacionObjetivo));
        totalDestino.setNumMuestras(sumarMuestras(totalesFuente));
        totalDestino.setNumIncidencias(sumarIncidencias(totalesFuente));
        totalDestino.setEvidenciaAcumulada(sumarEvidencias(totalesFuente));
    }

    private void recalcularTotalGlobal(TotalSoftSkillPorAlumno totalGlobal,
                                       List<? extends SoftSkillTotalizable> totalesFuente) {
        BigDecimal puntuacionObjetivo = calcularObjetivoGlobal(totalesFuente);
        BigDecimal puntuacionActual = totalGlobal.getPuntuacionTotal();

        // Si ya existe total global, lo acercamos gradualmente al objetivo calculado.
        // Con 0.25 el valor general absorbe un 25% de la diferencia en cada recalculo:
        // suficiente para reflejar tendencia, pero evitando bandazos por cambios locales.
        BigDecimal nuevaPuntuacion = puntuacionActual == null
                ? puntuacionObjetivo
                : puntuacionActual.add(
                        puntuacionObjetivo.subtract(puntuacionActual)
                                .multiply(GLOBAL_SMOOTHING_FACTOR)
                );

        totalGlobal.setPuntuacionTotal(acotarPuntuacion(nuevaPuntuacion));
        totalGlobal.setNumMuestras(sumarMuestras(totalesFuente));
        totalGlobal.setNumIncidencias(sumarIncidencias(totalesFuente));
        totalGlobal.setEvidenciaAcumulada(sumarEvidencias(totalesFuente));
    }

    private BigDecimal calcularObjetivoGlobal(List<? extends SoftSkillTotalizable> totalesFuente) {
        BigDecimal sumaPonderada = ZERO;
        BigDecimal sumaPesos = ZERO;

        for (SoftSkillTotalizable totalFuente : totalesFuente) {
            BigDecimal puntuacion = totalFuente.getPuntuacionTotal() != null
                    ? totalFuente.getPuntuacionTotal()
                    : ZERO;
            // Los agregados con mas muestras pesan mas en el total global, pero con
            // rendimientos decrecientes para que un unico curso o grupo no monopolice
            // el resultado general del alumno.
            BigDecimal peso = calcularPeso(totalFuente.getNumMuestras());

            if (peso.compareTo(ZERO) <= 0) {
                continue;
            }

            sumaPonderada = sumaPonderada.add(puntuacion.multiply(peso));
            sumaPesos = sumaPesos.add(peso);
        }

        if (sumaPesos.compareTo(ZERO) == 0) {
            return ZERO;
        }

        return sumaPonderada.divide(sumaPesos, 4, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularPeso(Long numMuestras) {
        long muestras = numMuestras != null ? numMuestras : 0L;
        if (muestras <= 0) {
            return ZERO;
        }

        return BigDecimal.valueOf(Math.sqrt(muestras));
    }

    private long sumarMuestras(List<? extends SoftSkillTotalizable> totalesFuente) {
        long total = 0L;
        for (SoftSkillTotalizable totalFuente : totalesFuente) {
            total += totalFuente.getNumMuestras() != null ? totalFuente.getNumMuestras() : 0L;
        }
        return total;
    }

    private long sumarIncidencias(List<? extends SoftSkillTotalizable> totalesFuente) {
        long total = 0L;
        for (SoftSkillTotalizable totalFuente : totalesFuente) {
            total += totalFuente.getNumIncidencias() != null ? totalFuente.getNumIncidencias() : 0L;
        }
        return total;
    }

    private BigDecimal sumarEvidencias(List<? extends SoftSkillTotalizable> totalesFuente) {
        BigDecimal total = ZERO;
        for (SoftSkillTotalizable totalFuente : totalesFuente) {
            if (totalFuente.getEvidenciaAcumulada() != null) {
                total = total.add(totalFuente.getEvidenciaAcumulada());
            }
        }
        return total;
    }

    private BigDecimal acotarPuntuacion(BigDecimal puntuacion) {
        if (puntuacion.compareTo(ZERO) < 0) {
            return ZERO;
        }
        if (puntuacion.compareTo(MAX_SCORE) > 0) {
            return MAX_SCORE;
        }
        return puntuacion;
    }
}
