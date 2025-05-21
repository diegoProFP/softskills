package es.ggm.infor.softskills.dao;

import es.ggm.infor.softskills.model.SoftSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SoftSkillRepository extends JpaRepository<SoftSkill, Long> {
    @Query("SELECT s FROM SoftSkill s JOIN s.cursos c WHERE c.id = :cursoId")
    List<SoftSkill> findByCursoId(@Param("cursoId") Long cursoId);
    
}
