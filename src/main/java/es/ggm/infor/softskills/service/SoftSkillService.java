package es.ggm.infor.softskills.service;

import es.ggm.infor.softskills.dao.CursoRepository;
import es.ggm.infor.softskills.dao.MuestraSoftSkillRepository;
import es.ggm.infor.softskills.dao.SoftSkillRepository;
import es.ggm.infor.softskills.dto.MuestraRequest;
import es.ggm.infor.softskills.model.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SoftSkillService implements ISoftSkillService {
    private static final Logger log = LoggerFactory.getLogger(SoftSkillService.class);

     private final SoftSkillRepository softSkillRepository;

    private final CursoRepository cursoRepository;
    private final IAlumnoService alumnoService;
    private final MuestraSoftSkillRepository muestraRepository;



     @Override
     public List<SoftSkill> getAllSoftSkills() {
         return softSkillRepository.findAll();
     }

     @Override
     public SoftSkill getSoftSkillById(Long id) {
         return softSkillRepository.findById(id)
                 .orElseThrow(() -> new RuntimeException("Soft Skill not found with id: " + id));
     }
    @Override
    @Transactional
    public void insertarMuestra(MuestraRequest request) {
        log.debug("Insertando muestra para curso {}, alumno {}, skill {}, valor {}", request.getCursoId(), request.getAlumnoId(), request.getSoftSkillId(), request.getValor());

        Curso curso = cursoRepository.findById(request.getCursoId())
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado: " + request.getCursoId()));


        Alumno alumno = alumnoService.getAlumnoById(request.getAlumnoId());

        SoftSkill softSkill = softSkillRepository.findById(request.getSoftSkillId())
                .orElseThrow(() -> new IllegalArgumentException("SoftSkill no encontrada: " + request.getSoftSkillId()));

        boolean pertenece = curso.getAlumnos().stream().anyMatch(a -> a.getId().equals(alumno.getId()));
        if (!pertenece) {
            log.warn("El alumno {} no pertenece al curso {}", alumno.getId(), curso.getId());
            throw new SecurityException("El alumno no pertenece al curso indicado");
        }

        Profesor profesor = Profesor.builder().id(request.getProfesorId()).build();

        MuestraSoftSkill muestra = MuestraSoftSkill.builder()
                .curso(curso)
                .alumno(alumno)
                .profesor(profesor)
                .softSkill(softSkill)
                .valor(request.getValor())
                .fecha(LocalDateTime.now())
                .build();

        muestraRepository.save(muestra);
        log.info("Muestra registrada con éxito: {}", muestra);
    }
}
