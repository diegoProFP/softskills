package es.ggm.infor.softskills.dto;

import java.util.ArrayList;
import java.util.List;

public class DetalleMuestrasSoftSkillAlumnoDTO {
    private Long alumnoId;
    private String alumnoNombre;
    private SoftSkillResumenDTO softSkill;
    private Long numMuestras;
    private List<MuestrasPorCursoDTO> cursos = new ArrayList<>();

    public Long getAlumnoId() {
        return alumnoId;
    }

    public void setAlumnoId(Long alumnoId) {
        this.alumnoId = alumnoId;
    }

    public String getAlumnoNombre() {
        return alumnoNombre;
    }

    public void setAlumnoNombre(String alumnoNombre) {
        this.alumnoNombre = alumnoNombre;
    }

    public SoftSkillResumenDTO getSoftSkill() {
        return softSkill;
    }

    public void setSoftSkill(SoftSkillResumenDTO softSkill) {
        this.softSkill = softSkill;
    }

    public Long getNumMuestras() {
        return numMuestras;
    }

    public void setNumMuestras(Long numMuestras) {
        this.numMuestras = numMuestras;
    }

    public List<MuestrasPorCursoDTO> getCursos() {
        return cursos;
    }

    public void setCursos(List<MuestrasPorCursoDTO> cursos) {
        this.cursos = cursos;
    }
}
