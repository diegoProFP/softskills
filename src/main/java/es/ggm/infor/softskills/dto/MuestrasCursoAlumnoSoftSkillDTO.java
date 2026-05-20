package es.ggm.infor.softskills.dto;

import java.util.ArrayList;
import java.util.List;

public class MuestrasCursoAlumnoSoftSkillDTO {
    private Long cursoId;
    private String cursoNombre;
    private Long alumnoId;
    private String alumnoNombre;
    private SoftSkillResumenDTO softSkill;
    private Long numMuestras;
    private List<MuestraSoftSkillDetalleDTO> muestras = new ArrayList<>();

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }

    public String getCursoNombre() {
        return cursoNombre;
    }

    public void setCursoNombre(String cursoNombre) {
        this.cursoNombre = cursoNombre;
    }

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

    public List<MuestraSoftSkillDetalleDTO> getMuestras() {
        return muestras;
    }

    public void setMuestras(List<MuestraSoftSkillDetalleDTO> muestras) {
        this.muestras = muestras;
    }
}
