package es.ggm.infor.softskills.service;

import es.ggm.infor.softskills.model.Profesor;
import es.ggm.infor.softskills.dao.ProfesorRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
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

    @Override
    public Profesor crearProfesor(Profesor profesor) {
        if (profesorRepository.existsById(profesor.getId())) {
            throw new EntityExistsException("El profesor ya existe: " + profesor.getId());
        }
        return profesorRepository.save(profesor);
    }

    @Override
    public Profesor actualizarProfesor(Long id, boolean administrador) {
        Profesor profesor = profesorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Profesor no encontrado: " + id));

        profesor.setAdministrador(administrador);
        return profesorRepository.save(profesor);
    }
}
