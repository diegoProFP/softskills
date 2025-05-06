package es.ggm.infor.softskills.service;

import es.ggm.infor.moodleintegration.dto.AlumnoMoodleDTO;
import es.ggm.infor.softskills.dao.AlumnoRepository;
import es.ggm.infor.softskills.model.Alumno;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AlumnoService implements IAlumnoService {


    private final AlumnoRepository alumnoRepository;

    public AlumnoService(AlumnoRepository alumnoRepository) {
        this.alumnoRepository = alumnoRepository;
    }

    @Override
    public List<Alumno> insertarAlumnosSiNoExisten(List<AlumnoMoodleDTO> alumnosMoodle) {
        List<Long> idsMoodle = alumnosMoodle.stream().map(dto -> dto.id).toList();

        List<Alumno> existentes = alumnoRepository.findAllById(idsMoodle);
        Set<Long> existentesIds = existentes.stream().map(Alumno::getId).collect(Collectors.toSet());

        List<Alumno> nuevos = (List<Alumno>) alumnosMoodle.stream()
                .filter(dto -> !existentesIds.contains(dto.id))
                .map(dto -> Alumno.builder().id(dto.id).build())
                .toList();

        alumnoRepository.saveAll(nuevos);

        List<Alumno> todos = new ArrayList<>();
        todos.addAll(existentes);
        todos.addAll(nuevos);
        return todos;
    }
}