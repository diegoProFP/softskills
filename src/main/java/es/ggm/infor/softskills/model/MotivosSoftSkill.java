package es.ggm.infor.softskills.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
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

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "SOFT_SKILL_ID")
    private SoftSkill softSkill;
}
