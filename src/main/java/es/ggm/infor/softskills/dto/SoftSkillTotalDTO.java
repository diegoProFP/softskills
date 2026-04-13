package es.ggm.infor.softskills.dto;

import java.math.BigDecimal;

public class SoftSkillTotalDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private BigDecimal puntuacionTotal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPuntuacionTotal() {
        return puntuacionTotal;
    }

    public void setPuntuacionTotal(BigDecimal puntuacionTotal) {
        this.puntuacionTotal = puntuacionTotal;
    }
}
