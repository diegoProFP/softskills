package es.ggm.infor.softskills.dto;

import es.ggm.infor.softskills.model.CodigoSoftSkill;
import es.ggm.infor.softskills.model.TipoMedicionSoftSkill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminSoftSkillResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private TipoMedicionSoftSkill tipoMedicion;
    private CodigoSoftSkill codigo;

    @Builder.Default
    private List<MotivoSoftSkillDTO> listaMotivos = new ArrayList<>();
}
