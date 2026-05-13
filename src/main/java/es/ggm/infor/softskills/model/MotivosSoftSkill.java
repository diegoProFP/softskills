package es.ggm.infor.softskills.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MOTIVO_SOFT_SKILL")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MotivosSoftSkill {

    @Id
    private Long id;

    private String motivo;

    @Column(name = "DESCRIPCION_CORTA", length = 160)
    private String descripcionCorta;

    @Column(name = "DESCRIPCION_LARGA", length = 1000)
    private String descripcionLarga;

    @Column(name = "VALOR_POR_DEFECTO")
    private Integer valorPorDefecto;

    @Enumerated(EnumType.STRING)
    @Column(name = "NIVEL_POR_DEFECTO")
    private NivelMuestraSoftSkill nivelPorDefecto;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "SOFT_SKILL_ID")
    private SoftSkill softSkill;
}
