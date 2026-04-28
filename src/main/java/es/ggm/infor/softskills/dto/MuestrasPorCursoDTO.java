package es.ggm.infor.softskills.dto;

import java.util.ArrayList;
import java.util.List;

public class MuestrasPorCursoDTO {
    private Long cursoId;
    private String cursoNombre;
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
