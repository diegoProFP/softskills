package es.ggm.infor.softskills.service.strategy;

import es.ggm.infor.softskills.model.SoftSkill;
import es.ggm.infor.softskills.model.TipoMedicionSoftSkill;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class SoftSkillTotalStrategyResolver {

    private final Map<TipoMedicionSoftSkill, SoftSkillTotalStrategy> strategies;

    public SoftSkillTotalStrategyResolver(List<SoftSkillTotalStrategy> strategyList) {
        this.strategies = new EnumMap<>(TipoMedicionSoftSkill.class);
        for (SoftSkillTotalStrategy strategy : strategyList) {
            this.strategies.put(strategy.getTipoMedicion(), strategy);
        }
    }

    public SoftSkillTotalStrategy resolve(SoftSkill softSkill) {
        TipoMedicionSoftSkill tipoMedicion = softSkill.getTipoMedicion();
        if (tipoMedicion == null) {
            tipoMedicion = TipoMedicionSoftSkill.PENALIZACION_POR_TRAMOS;
        }

        SoftSkillTotalStrategy strategy = strategies.get(tipoMedicion);
        if (strategy == null) {
            throw new IllegalStateException("No existe estrategia para el tipo de medicion " + tipoMedicion);
        }

        return strategy;
    }
}
