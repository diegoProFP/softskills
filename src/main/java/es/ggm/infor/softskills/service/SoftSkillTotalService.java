package es.ggm.infor.softskills.service;

import es.ggm.infor.softskills.dao.TotalSoftSkillRepository;
import es.ggm.infor.softskills.model.Alumno;
import es.ggm.infor.softskills.model.Curso;
import es.ggm.infor.softskills.model.MuestraSoftSkill;
import es.ggm.infor.softskills.model.SoftSkill;
import es.ggm.infor.softskills.model.TotalSoftSkillPorAlumno;
import es.ggm.infor.softskills.model.TotalSoftSkillPorAlumnoCurso;
import es.ggm.infor.softskills.dao.TotalSoftSkillPorAlumnoCursoRepository;
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
    // Se recalcula a partir de los totales por curso y se suaviza para que el indicador
    // general evolucione de forma más estable durante el piloto anual.
    private static final BigDecimal GLOBAL_SMOOTHING_FACTOR = new BigDecimal("0.25");
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal MAX_SCORE = new BigDecimal("10.00");

    private final TotalSoftSkillRepository totalSoftSkillRepository;
    private final TotalSoftSkillPorAlumnoCursoRepository totalSoftSkillPorAlumnoCursoRepository;
    private final SoftSkillTotalStrategyResolver strategyResolver;

    @Transactional
    public void aplicarNuevaMuestra(MuestraSoftSkill muestra) {
        Alumno alumno = muestra.getAlumno();
        Curso curso = muestra.getCurso();
        SoftSkill softSkill = muestra.getSoftSkill();
        SoftSkillTotalStrategy strategy = strategyResolver.resolve(softSkill);

        actualizarTotalPorCurso(alumno, curso, softSkill, muestra, strategy);
        actualizarTotalGlobal(alumno, softSkill);
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

    private void actualizarTotalGlobal(Alumno alumno, SoftSkill softSkill) {
        List<TotalSoftSkillPorAlumnoCurso> totalesPorCurso = totalSoftSkillPorAlumnoCursoRepository
                .findByAlumnoAndSoftSkill(alumno, softSkill);

        if (totalesPorCurso.isEmpty()) {
            return;
        }

        Optional<TotalSoftSkillPorAlumno> totalExistente = totalSoftSkillRepository.findByAlumnoAndSoftSkill(alumno, softSkill);

        TotalSoftSkillPorAlumno total = totalExistente.orElseGet(() -> TotalSoftSkillPorAlumno.builder()
                .alumno(alumno)
                .softSkill(softSkill)
                .build());

        recalcularTotalGlobal(total, totalesPorCurso);
        totalSoftSkillRepository.save(total);
    }

    private void recalcularTotalGlobal(TotalSoftSkillPorAlumno totalGlobal,
                                       List<TotalSoftSkillPorAlumnoCurso> totalesPorCurso) {
        BigDecimal puntuacionObjetivo = calcularObjetivoGlobal(totalesPorCurso);
        BigDecimal puntuacionActual = totalGlobal.getPuntuacionTotal();

        // Si ya existe total global, lo acercamos gradualmente al objetivo calculado.
        // Con 0.25 el valor general absorbe un 25% de la diferencia en cada recálculo:
        // suficiente para reflejar tendencia, pero evitando bandazos por cambios locales.
        BigDecimal nuevaPuntuacion = puntuacionActual == null
                ? puntuacionObjetivo
                : puntuacionActual.add(
                        puntuacionObjetivo.subtract(puntuacionActual)
                                .multiply(GLOBAL_SMOOTHING_FACTOR)
                  );

        totalGlobal.setPuntuacionTotal(acotarPuntuacion(nuevaPuntuacion));
        totalGlobal.setNumMuestras(sumarMuestras(totalesPorCurso));
        totalGlobal.setNumIncidencias(sumarIncidencias(totalesPorCurso));
    }

    private BigDecimal calcularObjetivoGlobal(List<TotalSoftSkillPorAlumnoCurso> totalesPorCurso) {
        BigDecimal sumaPonderada = ZERO;
        BigDecimal sumaPesos = ZERO;

        for (TotalSoftSkillPorAlumnoCurso totalCurso : totalesPorCurso) {
            BigDecimal puntuacion = totalCurso.getPuntuacionTotal() != null
                    ? totalCurso.getPuntuacionTotal()
                    : ZERO;
            // Los cursos con más muestras pesan más en el total global, pero con
            // rendimientos decrecientes para que un único curso muy medido no monopolice
            // el resultado general del alumno.
            BigDecimal peso = calcularPeso(totalCurso.getNumMuestras());

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

    private long sumarMuestras(List<TotalSoftSkillPorAlumnoCurso> totalesPorCurso) {
        long total = 0L;
        for (TotalSoftSkillPorAlumnoCurso totalCurso : totalesPorCurso) {
            total += totalCurso.getNumMuestras() != null ? totalCurso.getNumMuestras() : 0L;
        }
        return total;
    }

    private long sumarIncidencias(List<TotalSoftSkillPorAlumnoCurso> totalesPorCurso) {
        long total = 0L;
        for (TotalSoftSkillPorAlumnoCurso totalCurso : totalesPorCurso) {
            total += totalCurso.getNumIncidencias() != null ? totalCurso.getNumIncidencias() : 0L;
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
