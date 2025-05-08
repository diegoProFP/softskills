package es.ggm.infor.softskills.service;

import es.ggm.infor.moodleintegration.exceptions.GeneralMoodleException;
import es.ggm.infor.softskills.model.Curso;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ICursoService {

//    List<Curso> getCursos();

    @Transactional
    void registrarCurso(String token, Long cursoId, Long idProfesor) throws GeneralMoodleException;

    /**
     * Busca los cursos que hay en el sistema y los une con los cursos que existen en Moodle. La finalidad es que se pueda saber qué cursos del usuario están ya volcados en nuestro sistema, y cuáles no.
     * El servicio buscará los cursos de Moodle, y luego buscará los cursos en el sistema que coinciden por ID.
     *
     * @return Se devolverá una lista de cursos, tanto los existentes en el sistema, como los que no.
     */
    List<Curso> getCursosDelProfesor(String token, Long idProfesor);

    @Transactional(readOnly = true)
    Curso obtenerCursoConAlumnos(String token, Long cursoId) throws GeneralMoodleException;

//    Curso getCursoById(Long cursoId);
//
//    void incluirCursoNuevo(Curso infoCurso);

}
