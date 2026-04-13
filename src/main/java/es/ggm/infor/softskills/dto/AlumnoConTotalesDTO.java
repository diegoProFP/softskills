package es.ggm.infor.softskills.dto;

import java.util.ArrayList;
import java.util.List;

public class AlumnoConTotalesDTO {
    private Long id;
    private String nombre;
    private List<SoftSkillTotalDTO> totalesPorSkill = new ArrayList<>();

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

    public List<SoftSkillTotalDTO> getTotalesPorSkill() {
        return totalesPorSkill;
    }

    public void setTotalesPorSkill(List<SoftSkillTotalDTO> totalesPorSkill) {
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
}
