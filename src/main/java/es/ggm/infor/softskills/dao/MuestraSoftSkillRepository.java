package es.ggm.infor.softskills.dao;

import es.ggm.infor.softskills.model.MuestraSoftSkill;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MuestraSoftSkillRepository extends JpaRepository<MuestraSoftSkill, Long> {

    @EntityGraph(attributePaths = {"curso", "profesor", "softSkill", "alumno", "motivoPredefinido"})
    List<MuestraSoftSkill> findByAlumno_IdAndSoftSkill_IdOrderByFechaDesc(Long alumnoId, Long softSkillId);

    @EntityGraph(attributePaths = {"curso", "profesor", "softSkill", "alumno", "motivoPredefinido"})
    List<MuestraSoftSkill> findByCurso_IdAndAlumno_IdAndSoftSkill_IdOrderByFechaDesc(Long cursoId, Long alumnoId, Long softSkillId);

    @EntityGraph(attributePaths = {"curso", "profesor", "softSkill", "alumno", "motivoPredefinido"})
    List<MuestraSoftSkill> findByCurso_IdAndAlumno_IdAndSoftSkill_IdOrderByFechaAscIdAsc(Long cursoId, Long alumnoId, Long softSkillId);
}
