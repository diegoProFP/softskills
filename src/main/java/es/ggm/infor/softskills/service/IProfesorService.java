package es.ggm.infor.softskills.service;

import es.ggm.infor.softskills.model.Profesor;
import java.util.List;

public interface IProfesorService {

    List<Profesor> getAllProfesores();

    Profesor getProfesorById(Long id);
}
