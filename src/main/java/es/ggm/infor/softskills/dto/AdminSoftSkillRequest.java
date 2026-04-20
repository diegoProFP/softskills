package es.ggm.infor.softskills.dto;

import es.ggm.infor.softskills.model.CodigoSoftSkill;
import es.ggm.infor.softskills.model.TipoMedicionSoftSkill;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class AdminSoftSkillRequest {

    @NotBlank(message = "El nombre de la soft skill es obligatorio.")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El tipo de medicion es obligatorio.")
    private TipoMedicionSoftSkill tipoMedicion;

    @NotNull(message = "La clave de la soft skill es obligatoria.")
    private CodigoSoftSkill codigo;

    @Valid
    @Builder.Default
    private List<MotivoSoftSkillDTO> listaMotivos = new ArrayList<>();
}
