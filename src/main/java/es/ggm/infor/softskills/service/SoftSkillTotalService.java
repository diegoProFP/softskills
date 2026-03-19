package es.ggm.infor.softskills.service;

import es.ggm.infor.softskills.dao.TotalSoftSkillRepository;
import es.ggm.infor.softskills.model.Alumno;
import es.ggm.infor.softskills.model.MuestraSoftSkill;
import es.ggm.infor.softskills.model.SoftSkill;
import es.ggm.infor.softskills.model.TotalSoftSkillPorAlumno;
import es.ggm.infor.softskills.service.strategy.SoftSkillTotalStrategy;
import es.ggm.infor.softskills.service.strategy.SoftSkillTotalStrategyResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SoftSkillTotalService {

    private final TotalSoftSkillRepository totalSoftSkillRepository;
    private final SoftSkillTotalStrategyResolver strategyResolver;

    @Transactional
    public TotalSoftSkillPorAlumno aplicarNuevaMuestra(MuestraSoftSkill muestra) {
        Alumno alumno = muestra.getAlumno();
        SoftSkill softSkill = muestra.getSoftSkill();

        Optional<TotalSoftSkillPorAlumno> totalExistente = totalSoftSkillRepository.findByAlumnoAndSoftSkill(alumno, softSkill);

        TotalSoftSkillPorAlumno total = totalExistente.orElseGet(() -> TotalSoftSkillPorAlumno.builder()
                .alumno(alumno)
                .softSkill(softSkill)
                .build());

        SoftSkillTotalStrategy strategy = strategyResolver.resolve(softSkill);
        strategy.aplicarAlta(total, muestra);

        return totalSoftSkillRepository.save(total);
    }
}
