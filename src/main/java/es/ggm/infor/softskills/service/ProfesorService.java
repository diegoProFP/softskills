package es.ggm.infor.softskills.service;

import es.ggm.infor.softskills.model.Profesor;
import es.ggm.infor.softskills.dao.ProfesorRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProfesorService implements IProfesorService {

    private final ProfesorRepository profesorRepository;

    public ProfesorService(ProfesorRepository profesorRepository) {
        this.profesorRepository = profesorRepository;
    }

    @Override
    public List<Profesor> getAllProfesores() {
        return profesorRepository.findAll();
    }

    @Override
    public Profesor getProfesorById(Long id) {
        return profesorRepository.findById(id)
                .orElse(null);
    }
}
