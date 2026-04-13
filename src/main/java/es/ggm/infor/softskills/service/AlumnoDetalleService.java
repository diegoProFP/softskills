package es.ggm.infor.softskills.service;

import es.ggm.infor.softskills.dao.AlumnoRepository;
import es.ggm.infor.softskills.dao.TotalSoftSkillRepository;
import es.ggm.infor.softskills.dto.AlumnoConTotalesDTO;
import es.ggm.infor.softskills.model.Alumno;
import es.ggm.infor.softskills.model.SoftSkill;
import es.ggm.infor.softskills.model.TotalSoftSkillPorAlumno;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Servicio para obtener un alumno con sus totales
@Service
public class AlumnoDetalleService {
    @Autowired
    private AlumnoRepository alumnoRepository;
    @Autowired private TotalSoftSkillRepository totalRepository;
    @Autowired private ISoftSkillService softSkillService;
    @Autowired private TotalesPorDefectoService totalesPorDefectoService;

    public AlumnoConTotalesDTO obtenerDetalleAlumno(Long idAlumno) {
        Alumno alumno = alumnoRepository.findById(idAlumno)
                .orElseThrow(() -> new EntityNotFoundException("Alumno no encontrado"));

        List<TotalSoftSkillPorAlumno> totales = totalRepository.findByAlumnoId(idAlumno);
        List<SoftSkill> softSkills = softSkillService.getAllSoftSkills();

        AlumnoConTotalesDTO dto = new AlumnoConTotalesDTO();
        dto.setId(alumno.getId());
        dto.setNombre(alumno.getNombre());

        Map<String, java.math.BigDecimal> totalesPorSkill = new LinkedHashMap<>();
        for (TotalSoftSkillPorAlumno total : totales) {
            totalesPorSkill.put(total.getSoftSkill().getNombre(), total.getPuntuacionTotal());
        }
        dto.setTotalesPorSkill(totalesPorDefectoService.completarConMaximosPorDefecto(totalesPorSkill, softSkills));

        return dto;
    }
}
