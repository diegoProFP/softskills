package es.ggm.infor.softskills.service;

import es.ggm.infor.softskills.model.SoftSkill;
import es.ggm.infor.softskills.model.TipoMedicionSoftSkill;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class TotalesPorDefectoService {

    private static final BigDecimal MAXIMO_POR_DEFECTO = new BigDecimal("10.00");

    public Map<String, BigDecimal> completarConMaximosPorDefecto(Map<String, BigDecimal> totalesExistentes,
                                                                 Collection<SoftSkill> softSkills) {
        Map<String, BigDecimal> totalesCompletos = totalesExistentes == null
                ? new LinkedHashMap<>()
                : new LinkedHashMap<>(totalesExistentes);
        aplicarMaximosPorDefecto(totalesCompletos, softSkills);
        return totalesCompletos;
    }

    public void aplicarMaximosPorDefecto(Map<String, BigDecimal> totales,
                                         Collection<SoftSkill> softSkills) {
        if (totales == null || softSkills == null || softSkills.isEmpty()) {
            return;
        }

        for (SoftSkill softSkill : softSkills) {
            if (softSkill == null || softSkill.getNombre() == null) {
                continue;
            }

            TipoMedicionSoftSkill tipoMedicion = softSkill.getTipoMedicion() != null
                    ? softSkill.getTipoMedicion()
                    : TipoMedicionSoftSkill.PENALIZACION_POR_TRAMOS;

            if (tipoMedicion == TipoMedicionSoftSkill.PENALIZACION_POR_TRAMOS) {
                totales.putIfAbsent(softSkill.getNombre(), MAXIMO_POR_DEFECTO);
            }
        }
    }
}
