package es.ggm.infor.softskills.model;

import es.ggm.infor.moodleintegration.dto.UsuarioMoodleDTO;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Usuario {
    @Id
    @Column(name = "ID")
    protected Long id;
}
