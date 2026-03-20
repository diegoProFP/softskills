package es.ggm.infor.softskills.service;

import es.ggm.infor.softskills.dao.TotalSoftSkillRepository;
import es.ggm.infor.softskills.model.Alumno;
import es.ggm.infor.softskills.model.Curso;
import es.ggm.infor.softskills.model.MuestraSoftSkill;
import es.ggm.infor.softskills.model.SoftSkill;
import es.ggm.infor.softskills.model.TotalSoftSkillPorAlumno;
import es.ggm.infor.softskills.model.TotalSoftSkillPorAlumnoCurso;
import es.ggm.infor.softskills.dao.TotalSoftSkillPorAlumnoCursoRepository;
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
    private final TotalSoftSkillPorAlumnoCursoRepository totalSoftSkillPorAlumnoCursoRepository;
    private final SoftSkillTotalStrategyResolver strategyResolver;

    @Transactional
    public void aplicarNuevaMuestra(MuestraSoftSkill muestra) {
        Alumno alumno = muestra.getAlumno();
        Curso curso = muestra.getCurso();
        SoftSkill softSkill = muestra.getSoftSkill();
        SoftSkillTotalStrategy strategy = strategyResolver.resolve(softSkill);

        actualizarTotalPorCurso(alumno, curso, softSkill, muestra, strategy);
        actualizarTotalGlobal(alumno, softSkill, muestra, strategy);
    }

    private void actualizarTotalPorCurso(Alumno alumno, Curso curso, SoftSkill softSkill,
                                         MuestraSoftSkill muestra, SoftSkillTotalStrategy strategy) {
        Optional<TotalSoftSkillPorAlumnoCurso> totalExistente = totalSoftSkillPorAlumnoCursoRepository
                .findByAlumnoAndCursoAndSoftSkill(alumno, curso, softSkill);

        TotalSoftSkillPorAlumnoCurso total = totalExistente.orElseGet(() -> TotalSoftSkillPorAlumnoCurso.builder()
                .alumno(alumno)
                .curso(curso)
                .softSkill(softSkill)
                .build());

        strategy.aplicarAlta(total, muestra);
        totalSoftSkillPorAlumnoCursoRepository.save(total);
    }

    private void actualizarTotalGlobal(Alumno alumno, SoftSkill softSkill,
                                       MuestraSoftSkill muestra, SoftSkillTotalStrategy strategy) {
        Optional<TotalSoftSkillPorAlumno> totalExistente = totalSoftSkillRepository.findByAlumnoAndSoftSkill(alumno, softSkill);

        TotalSoftSkillPorAlumno total = totalExistente.orElseGet(() -> TotalSoftSkillPorAlumno.builder()
                .alumno(alumno)
                .softSkill(softSkill)
                .build());

        strategy.aplicarAlta(total, muestra);
        totalSoftSkillRepository.save(total);
    }
}
