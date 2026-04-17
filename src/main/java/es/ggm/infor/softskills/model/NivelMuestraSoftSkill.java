package es.ggm.infor.softskills.model;

import java.math.BigDecimal;

public enum NivelMuestraSoftSkill {
    LEVE(new BigDecimal("0.50")),
    NORMAL(new BigDecimal("1.00")),
    SIGNIFICATIVA(new BigDecimal("2.00"));

    private final BigDecimal peso;

    NivelMuestraSoftSkill(BigDecimal peso) {
        this.peso = peso;
    }

    public BigDecimal getPeso() {
        return peso;
    }
}
