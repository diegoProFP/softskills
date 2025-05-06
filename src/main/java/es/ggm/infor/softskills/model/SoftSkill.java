package es.ggm.infor.softskills.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "SOFT_SKILL")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SoftSkill {
    @Id
    @GeneratedValue
    private Long id;

    private String nombre;
    private String descripcion;
}