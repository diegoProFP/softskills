package es.ggm.infor.softskills.dao;

import es.ggm.infor.softskills.model.SoftSkill;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SoftSkillRepository extends JpaRepository<SoftSkill, Long> {
    @Override
    @EntityGraph(attributePaths = "listaMotivos")
    List<SoftSkill> findAll();

    @Query("SELECT DISTINCT s FROM SoftSkill s JOIN s.cursos c LEFT JOIN FETCH s.listaMotivos WHERE c.id = :cursoId")
    List<SoftSkill> findByCursoId(@Param("cursoId") Long cursoId);

    @Query("SELECT DISTINCT s FROM SoftSkill s JOIN s.cursos c LEFT JOIN FETCH s.listaMotivos WHERE c.grupoAcademico.id = :grupoId")
    List<SoftSkill> findByGrupoId(@Param("grupoId") Long grupoId);

    @Query("SELECT DISTINCT s FROM SoftSkill s LEFT JOIN FETCH s.listaMotivos WHERE s.id = :id")
    Optional<SoftSkill> findByIdWithMotivos(@Param("id") Long id);
}
