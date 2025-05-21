package es.ggm.infor.softskills.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
}
