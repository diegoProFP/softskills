package es.ggm.infor.softskills.dto;

public class BorrarMuestraResponse {
    private boolean deleted;
    private Long muestraId;
    private Long cursoId;
    private Long alumnoId;
    private Long softSkillId;
    private TotalActualizadoDTO totalActualizado;

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Long getMuestraId() {
        return muestraId;
    }

    public void setMuestraId(Long muestraId) {
        this.muestraId = muestraId;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }

    public Long getAlumnoId() {
        return alumnoId;
    }

    public void setAlumnoId(Long alumnoId) {
        this.alumnoId = alumnoId;
    }

    public Long getSoftSkillId() {
        return softSkillId;
    }

    public void setSoftSkillId(Long softSkillId) {
        this.softSkillId = softSkillId;
    }

    public TotalActualizadoDTO getTotalActualizado() {
        return totalActualizado;
    }

    public void setTotalActualizado(TotalActualizadoDTO totalActualizado) {
        this.totalActualizado = totalActualizado;
    }
}
