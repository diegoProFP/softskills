package es.ggm.infor.softskills.service;

import es.ggm.infor.softskills.dao.AlumnoRepository;
import es.ggm.infor.softskills.dao.TotalSoftSkillRepository;
import es.ggm.infor.softskills.dto.AlumnoConTotalesDTO;
import es.ggm.infor.softskills.model.Alumno;
import es.ggm.infor.softskills.model.TotalSoftSkillPorAlumno;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// Servicio para obtener un alumno con sus totales
@Service
public class AlumnoDetalleService {
    @Autowired
    private AlumnoRepository alumnoRepository;
    @Autowired private TotalSoftSkillRepository totalRepository;

    public AlumnoConTotalesDTO obtenerDetalleAlumno(Long idAlumno) {
        Alumno alumno = alumnoRepository.findById(idAlumno)
                .orElseThrow(() -> new EntityNotFoundException("Alumno no encontrado"));

        List<TotalSoftSkillPorAlumno> totales = totalRepository.findByAlumnoId(idAlumno);

        AlumnoConTotalesDTO dto = new AlumnoConTotalesDTO();
        dto.setId(alumno.getId());
        dto.setNombre(alumno.getNombre());

        for (TotalSoftSkillPorAlumno total : totales) {
            dto.getTotalesPorSkill().put(total.getSoftSkill().getNombre(), total.getPuntuacionTotal());
        }

        return dto;
    }
}