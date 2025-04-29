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
    Long id;

    @Column(name = "NOMBRE", nullable = false, unique = true)
    private String nombre;

    @Column(name ="NOMBRE_CORTO")
    private String nombreCorto;

    @Transient
    private boolean registradoSk;

    @Column(name = "FECHA_ALTA")
    private LocalDateTime fechaAlta;

}
