package es.ggm.infor.softskills.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"alumno_id", "softSkill_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TotalSoftSkillPorAlumno {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    private Alumno alumno;

    @ManyToOne(optional = false)
    private SoftSkill softSkill;

    private BigDecimal puntuacionTotal;

    @Builder.Default
    private Long numMuestras = 0L;

    @Builder.Default
    private Long numIncidencias = 0L;

    // getters y setters
}
