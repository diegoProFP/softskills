package es.ggm.infor.softskills.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data // Incluye getters, setters, toString, equals y hashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "CURSO")
public class Curso {

    @Id
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "PROFESOR_ID")
    private Profesor profesor;

    @ManyToMany
    @JoinTable(
            name = "CURSO_ALUMNO",
            joinColumns = @JoinColumn(name = "CURSO_ID"),
            inverseJoinColumns = @JoinColumn(name = "ALUMNO_ID")
    )
    private List<Alumno> alumnos = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "CURSO_SOFT_SKILL",
            joinColumns = @JoinColumn(name = "CURSO_ID"),
            inverseJoinColumns = @JoinColumn(name = "SOFT_SKILL_ID")
    )
    private List<SoftSkill> softSkills = new ArrayList<>();


    @Transient
    private String nombreCorto;

    @Transient
    private String nombreLargo;

    @Transient
    private String nombreVisible;

    @Transient
    private boolean registradoSk;

    @Column(name = "FECHA_ALTA")
    private LocalDateTime fechaAlta;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Profesor getProfesor() {
        return profesor;
    }

    public void setProfesor(Profesor profesor) {
        this.profesor = profesor;
    }

    public List<Alumno> getAlumnos() {
        return alumnos;
    }

    public void setAlumnos(List<Alumno> alumnos) {
        this.alumnos = alumnos;
    }

    public List<SoftSkill> getSoftSkills() {
        return softSkills;
    }

    public void setSoftSkills(List<SoftSkill> softSkills) {
        this.softSkills = softSkills;
    }

    public String getNombreCorto() {
        return nombreCorto;
    }

    public void setNombreCorto(String nombreCorto) {
        this.nombreCorto = nombreCorto;
    }

    public String getNombreLargo() {
        return nombreLargo;
    }

    public void setNombreLargo(String nombreLargo) {
        this.nombreLargo = nombreLargo;
    }

    public String getNombreVisible() {
        return nombreVisible;
    }

    public void setNombreVisible(String nombreVisible) {
        this.nombreVisible = nombreVisible;
    }

    public boolean isRegistradoSk() {
        return registradoSk;
    }

    public void setRegistradoSk(boolean registradoSk) {
        this.registradoSk = registradoSk;
    }

    public LocalDateTime getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDateTime fechaAlta) {
        this.fechaAlta = fechaAlta;
    }
}
