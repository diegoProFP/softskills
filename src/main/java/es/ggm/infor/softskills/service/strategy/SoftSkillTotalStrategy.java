package es.ggm.infor.softskills.service.strategy;

import es.ggm.infor.softskills.model.MuestraSoftSkill;
import es.ggm.infor.softskills.model.TipoMedicionSoftSkill;
import es.ggm.infor.softskills.model.TotalSoftSkillPorAlumno;

public interface SoftSkillTotalStrategy {

    TipoMedicionSoftSkill getTipoMedicion();

    void aplicarAlta(TotalSoftSkillPorAlumno total, MuestraSoftSkill muestra);
}
