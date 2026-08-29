package es.ggm.infor.softskills.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminGrupoAcademicoResponse {

    private Long id;
    private String nivel;
    private String cicloFormativo;
    private String grupo;
    private String cursoEscolar;
    private Long cursoMoodleGrupoId;
}
