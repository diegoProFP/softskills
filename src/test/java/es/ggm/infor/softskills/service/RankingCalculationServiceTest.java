package es.ggm.infor.softskills.service;

import es.ggm.infor.softskills.config.RankingMode;
import es.ggm.infor.softskills.config.RankingProperties;
import es.ggm.infor.softskills.dto.SoftSkillTotalDTO;
import es.ggm.infor.softskills.model.SoftSkill;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RankingCalculationServiceTest {

    @Test
    void calculaMediaPonderadaConMayorPesoParaPrioridadesMasCriticas() {
        RankingProperties properties = new RankingProperties();
        properties.setMode(RankingMode.WEIGHTED_AVERAGE);
        RankingCalculationService service = new RankingCalculationService(properties);

        SoftSkill critica = SoftSkill.builder().id(1L).prioridadRanking(1).build();
        SoftSkill media = SoftSkill.builder().id(2L).prioridadRanking(3).build();

        SoftSkillTotalDTO totalCritica = new SoftSkillTotalDTO();
        totalCritica.setId(1L);
        totalCritica.setPuntuacionTotal(new BigDecimal("8.00"));

        SoftSkillTotalDTO totalMedia = new SoftSkillTotalDTO();
        totalMedia.setId(2L);
        totalMedia.setPuntuacionTotal(new BigDecimal("10.00"));

        BigDecimal ranking = service.calcularRankingScore(
                List.of(critica, media),
                List.of(totalCritica, totalMedia),
                Map.of(1L, 5L, 2L, 5L)
        );

        assertEquals(new BigDecimal("8.5000"), ranking);
    }

    @Test
    void aplicaFactorConfianzaCuandoElModoLoRequiere() {
        RankingProperties properties = new RankingProperties();
        properties.setMode(RankingMode.WEIGHTED_AVERAGE_WITH_CONFIDENCE);
        properties.setConfidenceTargetSamples(20L);
        RankingCalculationService service = new RankingCalculationService(properties);

        SoftSkill skill = SoftSkill.builder().id(1L).prioridadRanking(3).build();
        SoftSkillTotalDTO total = new SoftSkillTotalDTO();
        total.setId(1L);
        total.setPuntuacionTotal(new BigDecimal("9.6000"));

        BigDecimal ranking = service.calcularRankingScore(
                List.of(skill),
                List.of(total),
                Map.of(1L, 2L)
        );

        assertEquals(new BigDecimal("7.7568"), ranking);
    }
}
