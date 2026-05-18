package es.ggm.infor.softskills.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import es.ggm.infor.softskills.model.NivelMuestraSoftSkill;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MuestraRequest {
    private Long profesorId;
    private Long cursoId;
    private Long alumnoId;
    private Long softSkillId;
    private Long motivoId;
    private int valor; // 1 o -1
    private NivelMuestraSoftSkill nivel;
    @JsonAlias("motivoComentario")
    private String motivo;
}
