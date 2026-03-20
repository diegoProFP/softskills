package es.ggm.infor.softskills.service.strategy;

import es.ggm.infor.softskills.model.MuestraSoftSkill;
import es.ggm.infor.softskills.model.SoftSkillTotalizable;
import es.ggm.infor.softskills.model.TipoMedicionSoftSkill;

public interface SoftSkillTotalStrategy {

    TipoMedicionSoftSkill getTipoMedicion();

    void aplicarAlta(SoftSkillTotalizable total, MuestraSoftSkill muestra);
}
