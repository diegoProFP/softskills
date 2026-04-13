package es.ggm.infor.softskills.service;

import es.ggm.infor.softskills.config.RankingMode;
import es.ggm.infor.softskills.config.RankingProperties;
import es.ggm.infor.softskills.dto.SoftSkillTotalDTO;
import es.ggm.infor.softskills.model.SoftSkill;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class RankingCalculationService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal BASE_CONFIDENCE_MULTIPLIER = new BigDecimal("0.7");
    private static final BigDecimal EXTRA_CONFIDENCE_MULTIPLIER = new BigDecimal("0.3");
    private static final MathContext MATH_CONTEXT = new MathContext(12, RoundingMode.HALF_UP);

    private final RankingProperties rankingProperties;

    public RankingCalculationService(RankingProperties rankingProperties) {
        this.rankingProperties = rankingProperties;
    }

    public BigDecimal calcularRankingScore(Collection<SoftSkill> softSkills,
                                           List<SoftSkillTotalDTO> totalesPorSkill,
                                           Map<Long, Long> muestrasPorSkill) {
        BigDecimal mediaPonderada = calcularMediaPonderada(softSkills, totalesPorSkill);
        if (rankingProperties.getMode() == RankingMode.WEIGHTED_AVERAGE) {
            return mediaPonderada;
        }

        BigDecimal factorConfianza = calcularFactorConfianza(sumarMuestras(muestrasPorSkill));
        BigDecimal multiplicador = BASE_CONFIDENCE_MULTIPLIER.add(
                EXTRA_CONFIDENCE_MULTIPLIER.multiply(factorConfianza, MATH_CONTEXT),
                MATH_CONTEXT
        );
        return mediaPonderada.multiply(multiplicador, MATH_CONTEXT).setScale(4, RoundingMode.HALF_UP);
    }

    BigDecimal calcularMediaPonderada(Collection<SoftSkill> softSkills, List<SoftSkillTotalDTO> totalesPorSkill) {
        if (softSkills == null || softSkills.isEmpty() || totalesPorSkill == null || totalesPorSkill.isEmpty()) {
            return ZERO.setScale(4, RoundingMode.HALF_UP);
        }

        Map<Long, BigDecimal> puntuacionPorSkill = totalesPorSkill.stream()
                .filter(total -> total.getId() != null && total.getPuntuacionTotal() != null)
                .collect(java.util.stream.Collectors.toMap(
                        SoftSkillTotalDTO::getId,
                        SoftSkillTotalDTO::getPuntuacionTotal,
                        (left, right) -> right
                ));

        BigDecimal sumaPonderada = ZERO;
        BigDecimal sumaPesos = ZERO;

        for (SoftSkill softSkill : softSkills) {
            if (softSkill == null || softSkill.getId() == null) {
                continue;
            }

            BigDecimal puntuacion = puntuacionPorSkill.get(softSkill.getId());
            if (puntuacion == null) {
                continue;
            }

            BigDecimal peso = calcularPeso(softSkill.getPrioridadRanking());
            sumaPonderada = sumaPonderada.add(puntuacion.multiply(peso, MATH_CONTEXT), MATH_CONTEXT);
            sumaPesos = sumaPesos.add(peso, MATH_CONTEXT);
        }

        if (sumaPesos.compareTo(ZERO) == 0) {
            return ZERO.setScale(4, RoundingMode.HALF_UP);
        }

        return sumaPonderada.divide(sumaPesos, 4, RoundingMode.HALF_UP);
    }

    long sumarMuestras(Map<Long, Long> muestrasPorSkill) {
        if (muestrasPorSkill == null || muestrasPorSkill.isEmpty()) {
            return 0L;
        }

        long total = 0L;
        for (Long muestras : muestrasPorSkill.values()) {
            total += muestras != null ? muestras : 0L;
        }
        return total;
    }

    BigDecimal calcularFactorConfianza(long numMuestrasTotales) {
        long objetivo = Math.max(1L, rankingProperties.getConfidenceTargetSamples());
        if (numMuestrasTotales <= 0L) {
            return ZERO.setScale(4, RoundingMode.HALF_UP);
        }

        double numerador = Math.log1p(numMuestrasTotales);
        double denominador = Math.log1p(objetivo);
        double factor = denominador <= 0d ? 1d : Math.min(1d, numerador / denominador);
        return BigDecimal.valueOf(factor).setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal calcularPeso(Integer prioridadRanking) {
        int prioridad = prioridadRanking == null || prioridadRanking <= 0 ? 3 : prioridadRanking;
        return BigDecimal.ONE.divide(BigDecimal.valueOf(prioridad), 8, RoundingMode.HALF_UP);
    }
}
