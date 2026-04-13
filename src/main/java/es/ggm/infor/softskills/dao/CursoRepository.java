package es.ggm.infor.softskills.dao;

import es.ggm.infor.softskills.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    List<Curso> findByProfesor_Id(Long idProfesor);
    List<Curso> findByGrupoAcademico_Id(Long grupoAcademicoId);
    Optional<Curso> findFirstByGrupoAcademico_Id(Long grupoAcademicoId);
}
