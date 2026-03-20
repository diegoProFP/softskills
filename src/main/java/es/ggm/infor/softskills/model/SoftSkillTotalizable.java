package es.ggm.infor.softskills.model;

import java.math.BigDecimal;

public interface SoftSkillTotalizable {

    BigDecimal getPuntuacionTotal();

    void setPuntuacionTotal(BigDecimal puntuacionTotal);

    Long getNumMuestras();

    void setNumMuestras(Long numMuestras);

    Long getNumIncidencias();

    void setNumIncidencias(Long numIncidencias);
}
