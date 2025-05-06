package es.ggm.infor.softskills.service;


import es.ggm.infor.moodleintegration.client.IMoodleClient;
import es.ggm.infor.moodleintegration.dto.AlumnoMoodleDTO;
import es.ggm.infor.moodleintegration.dto.CursoMoodleDTO;
import es.ggm.infor.moodleintegration.exceptions.GeneralMoodleException;
import es.ggm.infor.softskills.dao.CursoRepository;
import es.ggm.infor.softskills.dto.mapper.CursoMapper;
import es.ggm.infor.softskills.model.Alumno;
import es.ggm.infor.softskills.model.Curso;
import es.ggm.infor.softskills.model.Profesor;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
@Service
@RequiredArgsConstructor
public class CursoService implements ICursoService {

    @Autowired
    private final CursoRepository cursoRepository;
    @Autowired
    private final CursoMapper cursoMapper;
    @Autowired
    private final IMoodleClient moodleClient;
    @Autowired
    private final IAlumnoService alumnoService;


    private static final Logger logger = LoggerFactory.getLogger(CursoService.class);


    @Override
    @Transactional
    public void registrarCurso(String token, Long cursoId, Long idProfesor) throws GeneralMoodleException {
        Profesor profesor = Profesor.builder().id(idProfesor).build();
        Curso curso = Curso.builder()
                .id(cursoId)
                .profesor(profesor)
                .build();

        List<AlumnoMoodleDTO> alumnosMoodle = moodleClient.getAlumnos(token, cursoId);
        List<Alumno> alumnos = alumnoService.insertarAlumnosSiNoExisten(alumnosMoodle);
        curso.setAlumnos(alumnos);

        cursoRepository.save(curso);
    }


    @Override
    public List<Curso> getCursosDelProfesor(String token, Long idProfesor) {
        // Cursos en Moodle
        List<CursoMoodleDTO> cursosMoodle = null;
        try {
            cursosMoodle = moodleClient.getCursos(token, idProfesor);
        } catch (GeneralMoodleException e) {
            logger.error("Error al obtener los cursos de Moodle para el profesor " + idProfesor, e);
            throw new RuntimeException(e);
        }

        // Cursos en la BD
        List<Curso> cursosBD = cursoRepository.findByProfesor_Id(idProfesor);
        Map<Long, Curso> cursosBDMap = new HashMap<>();
        for (Curso curso : cursosBD) {
            curso.setRegistradoSk(true);
            cursosBDMap.put(curso.getId(), curso);
        }

        List<Curso> resultado = new ArrayList<>();

        for (CursoMoodleDTO dto : cursosMoodle) {
            Curso curso = cursosBDMap.get(dto.id);
            if (curso != null) {
                //Actualizo la info que viene de Moodle
                cursoMapper.updateFromDto(dto, curso);
            } else {
                curso = cursoMapper.fromDto(dto);
            }
            resultado.add(curso);
        }
        return resultado;
    }
}
