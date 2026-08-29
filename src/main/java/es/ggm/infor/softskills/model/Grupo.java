package es.ggm.infor.softskills.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "GRUPO",
        uniqueConstraints = @UniqueConstraint(columnNames = {"nivel", "cicloFormativo", "grupo", "cursoEscolar"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grupo {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String nivel;

    @Column(nullable = false)
    private String cicloFormativo;

    @Column(nullable = false)
    private String grupo;

    @Column(nullable = false)
    private String cursoEscolar;

    private Long cursoMoodleGrupoId;
}
