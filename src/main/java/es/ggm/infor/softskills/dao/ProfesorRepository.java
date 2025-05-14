package es.ggm.infor.softskills.dao;

import es.ggm.infor.softskills.model.Alumno;
import es.ggm.infor.softskills.model.Profesor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfesorRepository extends JpaRepository<Profesor, Long> {
}
