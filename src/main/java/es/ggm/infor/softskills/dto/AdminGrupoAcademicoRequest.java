package es.ggm.infor.softskills.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminGrupoAcademicoRequest {

    @Positive(message = "El ID del curso Moodle del grupo debe ser positivo.")
    private Long cursoMoodleGrupoId;
}
