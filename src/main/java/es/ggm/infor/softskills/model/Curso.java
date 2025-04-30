package es.ggm.infor.softskills.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

}
