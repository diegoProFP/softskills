package es.ggm.infor.softskills.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "ALUMNO")

@AllArgsConstructor
@SuperBuilder
@ToString
public class Alumno extends Usuario{

    @Transient
    private String nombre;

    @Transient
    private String username;

    @Transient
    private String email;

    @Transient
    @Builder.Default
    private Map<String, BigDecimal> totalesPorSkill = new HashMap<>();

    @OneToMany(mappedBy = "alumno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TotalSoftSkillPorAlumno> totalesSoftSkills;

    public Alumno() {
        super();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, BigDecimal> getTotalesPorSkill() {
        return totalesPorSkill;
    }

    public void setTotalesPorSkill(Map<String, BigDecimal> totalesPorSkill) {
        this.totalesPorSkill = totalesPorSkill;
    }
}
