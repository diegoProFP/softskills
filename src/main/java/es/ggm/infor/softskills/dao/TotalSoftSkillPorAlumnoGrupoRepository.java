package es.ggm.infor.softskills.dao;

import es.ggm.infor.softskills.model.Alumno;
import es.ggm.infor.softskills.model.Grupo;
import es.ggm.infor.softskills.model.SoftSkill;
import es.ggm.infor.softskills.model.TotalSoftSkillPorAlumnoGrupo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TotalSoftSkillPorAlumnoGrupoRepository extends JpaRepository<TotalSoftSkillPorAlumnoGrupo, Long> {
    Optional<TotalSoftSkillPorAlumnoGrupo> findByAlumnoAndGrupoAndSoftSkill(Alumno alumno, Grupo grupo, SoftSkill softSkill);
    List<TotalSoftSkillPorAlumnoGrupo> findByAlumnoAndSoftSkill(Alumno alumno, SoftSkill softSkill);
}
