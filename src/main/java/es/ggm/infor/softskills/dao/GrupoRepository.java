package es.ggm.infor.softskills.dao;

import es.ggm.infor.softskills.model.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GrupoRepository extends JpaRepository<Grupo, Long> {
    Optional<Grupo> findByNivelAndCicloFormativoAndGrupoAndCursoEscolar(
            String nivel, String cicloFormativo, String grupo, String cursoEscolar
    );
}
