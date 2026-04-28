package es.ggm.infor.softskills.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MuestraSoftSkillDetalleDTO {
    private Long id;
    private LocalDateTime fecha;
    private Integer valor;
    private String nivel;
    private BigDecimal pesoNivel;
    private String motivo;
    private Long profesorId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Integer getValor() {
        return valor;
    }

    public void setValor(Integer valor) {
        this.valor = valor;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public BigDecimal getPesoNivel() {
        return pesoNivel;
    }

    public void setPesoNivel(BigDecimal pesoNivel) {
        this.pesoNivel = pesoNivel;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Long getProfesorId() {
        return profesorId;
    }

    public void setProfesorId(Long profesorId) {
        this.profesorId = profesorId;
    }
}
