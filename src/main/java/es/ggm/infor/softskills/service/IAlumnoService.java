package es.ggm.infor.softskills.service;

import es.ggm.infor.moodleintegration.dto.AlumnoMoodleDTO;
import es.ggm.infor.softskills.model.Alumno;

import java.util.List;

public interface IAlumnoService {
    List<Alumno> insertarAlumnosSiNoExisten(List<AlumnoMoodleDTO> alumnosMoodle);

    List<Alumno> getAllAlumnos();

    Alumno getAlumnoById(Long id);
}