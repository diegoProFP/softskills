package es.ggm.infor.softskills.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "SOFT_SKILL")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SoftSkill {
    @Id
    @GeneratedValue
    private Long id;

    private String nombre;
    private String descripcion;
    //Por ahora el tipo de la SK, en desarrollo
    private int tipo = 1;

    // Identificador estable para que frontend pueda mapear iconos y categorias
    // sin depender del nombre visible de la soft skill.
    @Enumerated(EnumType.STRING)
    @Column(name = "CODIGO")
    @Builder.Default
    private CodigoSoftSkill codigo = CodigoSoftSkill.GENERICA;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_MEDICION")
    @Builder.Default
    private TipoMedicionSoftSkill tipoMedicion = TipoMedicionSoftSkill.PENALIZACION_POR_TRAMOS;

    @OneToMany
    private List<MotivosSoftSkill> listaMotivos;

    @JsonIgnore
    @ManyToMany(mappedBy = "softSkills", fetch = FetchType.LAZY)
    private List<Curso> cursos;


    @Override
    public String toString() {
        return "SoftSkill{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
