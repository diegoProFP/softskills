package es.ggm.infor.softskills.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MotivoSoftSkillDTO {

    private Long id;

    @NotBlank(message = "El motivo es obligatorio.")
    private String motivo;
}
