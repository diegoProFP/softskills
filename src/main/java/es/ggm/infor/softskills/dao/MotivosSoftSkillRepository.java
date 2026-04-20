package es.ggm.infor.softskills.dao;

import es.ggm.infor.softskills.model.MotivosSoftSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MotivosSoftSkillRepository extends JpaRepository<MotivosSoftSkill, Long> {

    @Query("SELECT MAX(m.id) FROM MotivosSoftSkill m")
    Optional<Long> findMaxId();
}
