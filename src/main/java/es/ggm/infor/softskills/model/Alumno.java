package es.ggm.infor.softskills.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "ALUMNO")
@Data
@AllArgsConstructor
@SuperBuilder
@ToString
public class Alumno extends Usuario{
}
