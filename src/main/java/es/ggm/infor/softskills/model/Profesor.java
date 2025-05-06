package es.ggm.infor.softskills.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "PROFESOR")
@Data
@AllArgsConstructor
@SuperBuilder
@ToString
public class Profesor extends Usuario{
}
