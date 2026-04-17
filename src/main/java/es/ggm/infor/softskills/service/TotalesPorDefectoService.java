package es.ggm.infor.softskills.service;

import es.ggm.infor.softskills.dto.SoftSkillTotalDTO;
import es.ggm.infor.softskills.model.SoftSkill;
import es.ggm.infor.softskills.model.TipoMedicionSoftSkill;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Service
public class TotalesPorDefectoService {

    private static final BigDecimal MAXIMO_POR_DEFECTO = new BigDecimal("10.00");
    private static final BigDecimal MINIMO_POR_DEFECTO = BigDecimal.ZERO;

    public Map<Long, BigDecimal> completarConMaximosPorDefecto(Map<Long, BigDecimal> totalesExistentes,
                                                               Collection<SoftSkill> softSkills) {
        Map<Long, BigDecimal> totalesCompletos = totalesExistentes == null
                ? new LinkedHashMap<>()
                : new LinkedHashMap<>(totalesExistentes);
        aplicarMaximosPorDefecto(totalesCompletos, softSkills);
        return totalesCompletos;
    }

    public List<SoftSkillTotalDTO> construirTotales(Collection<SoftSkill> softSkills,
                                                    Map<Long, BigDecimal> totalesExistentes) {
        Map<Long, BigDecimal> totalesCompletos = completarConMaximosPorDefecto(totalesExistentes, softSkills);
        List<SoftSkillTotalDTO> resultado = new ArrayList<>();
        if (softSkills == null || softSkills.isEmpty()) {
            return resultado;
        }

        for (SoftSkill softSkill : softSkills) {
            if (softSkill == null || softSkill.getId() == null) {
                continue;
            }

            BigDecimal puntuacion = totalesCompletos.get(softSkill.getId());
            if (puntuacion == null) {
                continue;
            }

            SoftSkillTotalDTO dto = new SoftSkillTotalDTO();
            dto.setId(softSkill.getId());
            dto.setCodigo(softSkill.getCodigo() != null ? softSkill.getCodigo().name() : null);
            dto.setNombre(softSkill.getNombre());
            dto.setDescripcion(softSkill.getDescripcion());
            dto.setTipoMedicion(resolverTipoMedicion(softSkill).name());
            dto.setPuntuacionTotal(puntuacion);
            resultado.add(dto);
        }

        return resultado;
    }

    public void aplicarMaximosPorDefecto(Map<Long, BigDecimal> totales,
                                         Collection<SoftSkill> softSkills) {
        if (totales == null || softSkills == null || softSkills.isEmpty()) {
            return;
        }

        for (SoftSkill softSkill : softSkills) {
            if (softSkill == null || softSkill.getNombre() == null) {
                continue;
            }

            TipoMedicionSoftSkill tipoMedicion = resolverTipoMedicion(softSkill);

            if (tipoMedicion == TipoMedicionSoftSkill.PENALIZACION_POR_TRAMOS) {
                totales.putIfAbsent(softSkill.getId(), MAXIMO_POR_DEFECTO);
            } else if (tipoMedicion == TipoMedicionSoftSkill.ACUMULACION_SATURADA) {
                totales.putIfAbsent(softSkill.getId(), MINIMO_POR_DEFECTO);
            }
        }
    }

    private TipoMedicionSoftSkill resolverTipoMedicion(SoftSkill softSkill) {
        return softSkill.getTipoMedicion() != null
                ? softSkill.getTipoMedicion()
                : TipoMedicionSoftSkill.PENALIZACION_POR_TRAMOS;
    }
}
