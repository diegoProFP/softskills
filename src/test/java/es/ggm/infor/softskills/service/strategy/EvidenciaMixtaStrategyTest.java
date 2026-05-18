package es.ggm.infor.softskills.service.strategy;

import es.ggm.infor.softskills.model.MuestraSoftSkill;
import es.ggm.infor.softskills.model.NivelMuestraSoftSkill;
import es.ggm.infor.softskills.model.TotalSoftSkillPorAlumnoCurso;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EvidenciaMixtaStrategyTest {

    private final EvidenciaMixtaStrategy strategy = new EvidenciaMixtaStrategy();

    @Test
    void parteDeCincoYSubeConEvidenciaPositiva() {
        TotalSoftSkillPorAlumnoCurso total = TotalSoftSkillPorAlumnoCurso.builder().build();

        strategy.aplicarAlta(total, muestra(1, NivelMuestraSoftSkill.NORMAL));

        assertEquals(new BigDecimal("5.80"), total.getPuntuacionTotal());
        assertEquals(1L, total.getNumMuestras());
    }

    @Test
    void parteDeCincoYBajaConEvidenciaNegativa() {
        TotalSoftSkillPorAlumnoCurso total = TotalSoftSkillPorAlumnoCurso.builder().build();

        strategy.aplicarAlta(total, muestra(-1, NivelMuestraSoftSkill.NORMAL));

        assertEquals(new BigDecimal("4.00"), total.getPuntuacionTotal());
        assertEquals(1L, total.getNumMuestras());
        assertEquals(1L, total.getNumIncidencias());
    }

    @Test
    void acotaLaPuntuacionEntreCeroYDiez() {
        TotalSoftSkillPorAlumnoCurso total = TotalSoftSkillPorAlumnoCurso.builder()
                .puntuacionTotal(new BigDecimal("9.50"))
                .build();

        strategy.aplicarAlta(total, muestra(1, NivelMuestraSoftSkill.SIGNIFICATIVA));
        assertEquals(new BigDecimal("10.00"), total.getPuntuacionTotal());

        total.setPuntuacionTotal(new BigDecimal("1.00"));
        strategy.aplicarAlta(total, muestra(-1, NivelMuestraSoftSkill.SIGNIFICATIVA));
        assertEquals(BigDecimal.ZERO, total.getPuntuacionTotal());
    }

    private MuestraSoftSkill muestra(int valor, NivelMuestraSoftSkill nivel) {
        return MuestraSoftSkill.builder()
                .valor(valor)
                .nivel(nivel)
                .build();
    }
}
