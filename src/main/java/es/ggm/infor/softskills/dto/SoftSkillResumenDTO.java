package es.ggm.infor.softskills.dto;

public class SoftSkillResumenDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String tipoMedicion;

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

    public String getTipoMedicion() {
        return tipoMedicion;
    }

    public void setTipoMedicion(String tipoMedicion) {
        this.tipoMedicion = tipoMedicion;
    }
}
