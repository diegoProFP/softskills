package es.ggm.infor.softskills.dto;

import es.ggm.infor.softskills.model.CodigoSoftSkill;
import es.ggm.infor.softskills.model.TipoMedicionSoftSkill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SoftSkillAdminOptionsDTO {

    private List<TipoMedicionSoftSkill> tiposMedicion;
    private List<CodigoSoftSkill> codigos;
}
