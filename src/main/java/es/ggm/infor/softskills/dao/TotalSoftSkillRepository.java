package es.ggm.infor.softskills.dao;

import es.ggm.infor.softskills.model.Alumno;
import es.ggm.infor.softskills.model.SoftSkill;
import es.ggm.infor.softskills.model.TotalSoftSkillPorAlumno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TotalSoftSkillRepository extends JpaRepository<TotalSoftSkillPorAlumno, Long> {
    Optional<TotalSoftSkillPorAlumno> findByAlumnoAndSoftSkill(Alumno alumno, SoftSkill softSkill);
    List<TotalSoftSkillPorAlumno> findByAlumnoId(Long alumnoId);
    List<TotalSoftSkillPorAlumno> findBySoftSkillIdOrderByPuntuacionTotalDesc(Long softSkillId);
}