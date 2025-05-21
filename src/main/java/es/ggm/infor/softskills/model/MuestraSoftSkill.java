package es.ggm.infor.softskills.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// ========== ENTIDAD MUESTRA SOFT SKILL ==========
@Entity
@Table(name = "MUESTRA_SOFT_SKILL")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class MuestraSoftSkill {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "CURSO_ID")
    private Curso curso;

    @ManyToOne
    @JoinColumn(name = "ALUMNO_ID")
    private Alumno alumno;

    @ManyToOne
    @JoinColumn(name = "SOFT_SKILL_ID")
    private SoftSkill softSkill;

    @ManyToOne
    @JoinColumn(name = "PROFESOR_ID")
    private Profesor profesor;

    @Column(name = "FECHA")
    private LocalDateTime fecha;

    @Column(name = "VALOR")
    private int valor; // 1 positivo, -1 negativo

    @Column(name = "COMENTARIO")
    private String motivoComentario;
}