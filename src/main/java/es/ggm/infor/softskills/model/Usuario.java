package es.ggm.infor.softskills.model;

import es.ggm.infor.moodleintegration.dto.UsuarioMoodleDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "USUARIO")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@ToString
public abstract class Usuario {
    @Id
    @Column(name = "ID")
    private Long id;
}
