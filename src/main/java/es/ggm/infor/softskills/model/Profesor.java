package es.ggm.infor.softskills.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "PROFESOR")
@Data
@AllArgsConstructor
@Builder
@ToString
public class Profesor extends Usuario{
}
