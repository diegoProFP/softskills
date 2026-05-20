package es.ggm.infor.softskills.dto;

import java.math.BigDecimal;

public class TotalActualizadoDTO {
    private Long alumnoId;
    private Long softSkillId;
    private BigDecimal puntuacionTotal;
    private Long numMuestras;

    public Long getAlumnoId() {
        return alumnoId;
    }

    public void setAlumnoId(Long alumnoId) {
        this.alumnoId = alumnoId;
    }

    public Long getSoftSkillId() {
        return softSkillId;
    }

    public void setSoftSkillId(Long softSkillId) {
        this.softSkillId = softSkillId;
    }

    public BigDecimal getPuntuacionTotal() {
        return puntuacionTotal;
    }

    public void setPuntuacionTotal(BigDecimal puntuacionTotal) {
        this.puntuacionTotal = puntuacionTotal;
    }

    public Long getNumMuestras() {
        return numMuestras;
    }

    public void setNumMuestras(Long numMuestras) {
        this.numMuestras = numMuestras;
    }
}
