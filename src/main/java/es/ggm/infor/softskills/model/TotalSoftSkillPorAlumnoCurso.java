package es.ggm.infor.softskills.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"alumno_id", "curso_id", "softSkill_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TotalSoftSkillPorAlumnoCurso implements SoftSkillTotalizable {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    private Alumno alumno;

    @ManyToOne(optional = false)
    private Curso curso;

    @ManyToOne(optional = false)
    private SoftSkill softSkill;

    private BigDecimal puntuacionTotal;

    @Builder.Default
    private Long numMuestras = 0L;

    @Builder.Default
    private Long numIncidencias = 0L;
}
