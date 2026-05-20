package es.ggm.infor.softskills.dto;

public class MuestraOperacionResponse {
    private MuestraSoftSkillDetalleDTO muestra;
    private TotalActualizadoDTO totalActualizado;

    public MuestraSoftSkillDetalleDTO getMuestra() {
        return muestra;
    }

    public void setMuestra(MuestraSoftSkillDetalleDTO muestra) {
        this.muestra = muestra;
    }

    public TotalActualizadoDTO getTotalActualizado() {
        return totalActualizado;
    }

    public void setTotalActualizado(TotalActualizadoDTO totalActualizado) {
        this.totalActualizado = totalActualizado;
    }
}
