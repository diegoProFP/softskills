package es.ggm.infor.softskills.service.strategy;

import es.ggm.infor.softskills.model.MuestraSoftSkill;
import es.ggm.infor.softskills.model.NivelMuestraSoftSkill;
import es.ggm.infor.softskills.model.TotalSoftSkillPorAlumnoCurso;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AcumulacionSaturadaStrategyTest {

    private final AcumulacionSaturadaStrategy strategy = new AcumulacionSaturadaStrategy();

    @Test
    void parteDeCeroYAplicaElPesoDelNivel() {
        TotalSoftSkillPorAlumnoCurso total = TotalSoftSkillPorAlumnoCurso.builder().build();

        strategy.aplicarAlta(total, muestraPositiva(NivelMuestraSoftSkill.SIGNIFICATIVA));

        assertEquals(new BigDecimal("2.00"), total.getEvidenciaAcumulada());
        assertEquals(new BigDecimal("5.0000"), total.getPuntuacionTotal());
        assertEquals(1L, total.getNumMuestras());
    }

    @Test
    void aplicaRendimientosDecrecientesConMasEvidencias() {
        TotalSoftSkillPorAlumnoCurso total = TotalSoftSkillPorAlumnoCurso.builder().build();

        strategy.aplicarAlta(total, muestraPositiva(NivelMuestraSoftSkill.NORMAL));
        strategy.aplicarAlta(total, muestraPositiva(NivelMuestraSoftSkill.NORMAL));
        strategy.aplicarAlta(total, muestraPositiva(NivelMuestraSoftSkill.NORMAL));

        assertEquals(new BigDecimal("3.00"), total.getEvidenciaAcumulada());
        assertEquals(new BigDecimal("6.0000"), total.getPuntuacionTotal());
        assertEquals(3L, total.getNumMuestras());
    }

    @Test
    void unaMuestraNegativaNoIncrementaLaEvidencia() {
        TotalSoftSkillPorAlumnoCurso total = TotalSoftSkillPorAlumnoCurso.builder().build();

        strategy.aplicarAlta(total, MuestraSoftSkill.builder()
                .valor(-1)
                .nivel(NivelMuestraSoftSkill.SIGNIFICATIVA)
                .build());

        assertEquals(BigDecimal.ZERO, total.getEvidenciaAcumulada());
        assertEquals(BigDecimal.ZERO, total.getPuntuacionTotal());
        assertEquals(1L, total.getNumMuestras());
        assertEquals(1L, total.getNumIncidencias());
    }

    private MuestraSoftSkill muestraPositiva(NivelMuestraSoftSkill nivel) {
        return MuestraSoftSkill.builder()
                .valor(1)
                .nivel(nivel)
                .build();
    }
}
