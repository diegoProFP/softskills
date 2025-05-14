package es.ggm.infor.softskills.dao;

import es.ggm.infor.softskills.model.MuestraSoftSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MuestraSoftSkillRepository extends JpaRepository<MuestraSoftSkill, Long> {}

