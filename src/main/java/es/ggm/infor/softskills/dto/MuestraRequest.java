package es.ggm.infor.softskills.dto;

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
    private int valor; // 1 o -1
}