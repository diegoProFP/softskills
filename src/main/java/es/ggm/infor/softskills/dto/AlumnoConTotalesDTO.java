package es.ggm.infor.softskills.dto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

// DTO para detalle de un alumno con mapa de soft skills
public class AlumnoConTotalesDTO {
    private Long id;
    private String nombre;
    private Map<String, BigDecimal> totalesPorSkill = new HashMap<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Map<String, BigDecimal> getTotalesPorSkill() {
        return totalesPorSkill;
    }

    public void setTotalesPorSkill(Map<String, BigDecimal> totalesPorSkill) {
        this.totalesPorSkill = totalesPorSkill;
    }

    @Override
    public String toString() {
        return "AlumnoConTotalesDTO{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", totalesPorSkill=" + totalesPorSkill +
                '}';
    }


    // getters y setters
}
