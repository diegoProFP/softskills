package es.ggm.infor.softskills.service;


import es.ggm.infor.softskills.dto.MuestraRequest;
import es.ggm.infor.softskills.dto.AdminSoftSkillRequest;
import es.ggm.infor.softskills.dto.BorrarMuestraResponse;
import es.ggm.infor.softskills.dto.MuestraOperacionResponse;
import es.ggm.infor.softskills.dto.MuestrasCursoAlumnoSoftSkillDTO;
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

    MuestrasCursoAlumnoSoftSkillDTO obtenerMuestrasPorCursoAlumnoSoftSkill(Long cursoId, Long alumnoId,
                                                                           Long softSkillId, Long usuarioId,
                                                                           boolean isAdmin);

    @Transactional
    MuestraOperacionResponse actualizarMuestra(Long muestraId, MuestraRequest request, Long usuarioId, boolean isAdmin);

    @Transactional
    BorrarMuestraResponse borrarMuestra(Long muestraId, Long cursoId, Long alumnoId, Long softSkillId,
                                        Long usuarioId, boolean isAdmin);

    List<SoftSkill> getSoftSkillsByCursoId(Long cursoId);

    @Transactional
    SoftSkill actualizarSoftSkill(Long id, AdminSoftSkillRequest request);
    // void saveSoftSkill(SoftSkill softSkill);
    // void deleteSoftSkill(Long id);
}
