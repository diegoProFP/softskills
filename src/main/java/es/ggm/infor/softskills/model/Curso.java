package es.ggm.infor.softskills.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
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

    @ManyToOne
    @JoinColumn(name = "GRUPO_ACADEMICO_ID")
    private Grupo grupoAcademico;

    @ManyToMany
    @JoinTable(
            name = "CURSO_ALUMNO",
            joinColumns = @JoinColumn(name = "CURSO_ID"),
            inverseJoinColumns = @JoinColumn(name = "ALUMNO_ID")
    )
    private List<Alumno> alumnos = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
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

    @Transient
    private boolean registrableEnSoftSkills;

    @Column(name = "IDNUMBER")
    private String idNumber;

    @Column(name = "FECHA_ALTA", columnDefinition = "datetime")
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

    public Grupo getGrupoAcademico() {
        return grupoAcademico;
    }

    public void setGrupoAcademico(Grupo grupoAcademico) {
        this.grupoAcademico = grupoAcademico;
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

    public boolean isRegistrableEnSoftSkills() {
        return registrableEnSoftSkills;
    }

    public void setRegistrableEnSoftSkills(boolean registrableEnSoftSkills) {
        this.registrableEnSoftSkills = registrableEnSoftSkills;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public LocalDateTime getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(LocalDateTime fechaAlta) {
        this.fechaAlta = fechaAlta;
    }
}
