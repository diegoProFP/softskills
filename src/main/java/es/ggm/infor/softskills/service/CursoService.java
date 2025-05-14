package es.ggm.infor.softskills.service;


import es.ggm.infor.moodleintegration.client.IMoodleClient;
import es.ggm.infor.moodleintegration.dto.AlumnoMoodleDTO;
import es.ggm.infor.moodleintegration.dto.CursoMoodleDTO;
import es.ggm.infor.moodleintegration.exceptions.GeneralMoodleException;
import es.ggm.infor.softskills.dao.CursoRepository;
import es.ggm.infor.softskills.dto.mapper.AlumnoMapper;
import es.ggm.infor.softskills.dto.mapper.CursoMapper;
import es.ggm.infor.softskills.exception.CursoYaRegistradoException;
import es.ggm.infor.softskills.model.Alumno;
import es.ggm.infor.softskills.model.Curso;
import es.ggm.infor.softskills.model.Profesor;
import es.ggm.infor.softskills.model.SoftSkill;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private final ISoftSkillService softSkillService;

    @Autowired
    private final AlumnoMapper alumnoMapper;


    private static final Logger logger = LoggerFactory.getLogger(CursoService.class);


    @Override
    @Transactional
    public void registrarCurso(String token, Long cursoId, Long idProfesor) throws GeneralMoodleException {

        if (cursoRepository.existsById(cursoId)) {
            throw new CursoYaRegistradoException("El curso con ID " + cursoId + " ya ha sido registrado previamente.");
        }

        Profesor profesor = Profesor.builder().id(idProfesor).build();
        Curso curso = Curso.builder()
                .id(cursoId)
                .profesor(profesor)
                .build();

        List<AlumnoMoodleDTO> alumnosMoodle = moodleClient.getAlumnos(token, cursoId);

        // Filtrar alumnos que no coincidan con el id del profesor
        alumnosMoodle.removeIf(alumno -> alumno.id.equals(idProfesor));

        List<Alumno> alumnos = alumnoService.insertarAlumnosSiNoExisten(alumnosMoodle);
        curso.setAlumnos(alumnos);

        // Añadir todas las soft skills existentes
        List<SoftSkill> todasLasSoftSkills = softSkillService.getAllSoftSkills();
        curso.setSoftSkills(todasLasSoftSkills);

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

    @Override
    @Transactional(readOnly = true)
    public Curso obtenerCursoConAlumnos(String token, Long cursoId) throws GeneralMoodleException {
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado: " + cursoId));

        rellenarDetallesCurso(token, cursoId, curso);

        List<AlumnoMoodleDTO> datosMoodle = moodleClient.getAlumnos(token, cursoId);
        Map<Long, AlumnoMoodleDTO> dtoMap = datosMoodle.stream()
                .collect(Collectors.toMap(dto -> dto.id, dto -> dto));

        for (Alumno alumno : curso.getAlumnos()) {
            AlumnoMoodleDTO dto = dtoMap.get(alumno.getId());
            if (dto != null) {
                alumnoMapper.updateFromDto(dto, alumno);
            }
        }

        return curso;
    }

    @Override
    public Curso getCursoById(Long cursoId) {
        return cursoRepository.findById(cursoId)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado: " + cursoId));
    }

    private void rellenarDetallesCurso(String token, Long cursoId, Curso curso) throws GeneralMoodleException {
        CursoMoodleDTO detallesCursoMoodle = moodleClient.getInfoCurso(token, cursoId);
        cursoMapper.updateFromDto(detallesCursoMoodle, curso);
    }
}
