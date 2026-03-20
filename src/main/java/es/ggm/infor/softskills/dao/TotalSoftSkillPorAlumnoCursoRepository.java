package es.ggm.infor.softskills.dao;

import es.ggm.infor.softskills.model.Alumno;
import es.ggm.infor.softskills.model.Curso;
import es.ggm.infor.softskills.model.SoftSkill;
import es.ggm.infor.softskills.model.TotalSoftSkillPorAlumnoCurso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TotalSoftSkillPorAlumnoCursoRepository extends JpaRepository<TotalSoftSkillPorAlumnoCurso, Long> {
    Optional<TotalSoftSkillPorAlumnoCurso> findByAlumnoAndCursoAndSoftSkill(Alumno alumno, Curso curso, SoftSkill softSkill);
    List<TotalSoftSkillPorAlumnoCurso> findByCursoIdAndAlumnoIdIn(Long cursoId, List<Long> alumnoIds);
    List<TotalSoftSkillPorAlumnoCurso> findByAlumnoAndSoftSkill(Alumno alumno, SoftSkill softSkill);
}
