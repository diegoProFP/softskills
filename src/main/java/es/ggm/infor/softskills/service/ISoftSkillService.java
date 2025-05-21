package es.ggm.infor.softskills.service;


import es.ggm.infor.softskills.dto.MuestraRequest;
import es.ggm.infor.softskills.model.SoftSkill;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public interface ISoftSkillService {

    // Define the methods for the service here
    // For example:
     List<SoftSkill> getAllSoftSkills();
     SoftSkill getSoftSkillById(Long id);

    @Transactional
    void insertarMuestra(MuestraRequest request);

    List<SoftSkill> getSoftSkillsByCursoId(Long cursoId);
    // void saveSoftSkill(SoftSkill softSkill);
    // void deleteSoftSkill(Long id);
}
