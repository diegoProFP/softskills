package es.ggm.infor.softskills.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

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
}
