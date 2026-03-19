package es.ggm.infor.softskills.service;

import es.ggm.infor.softskills.dao.AlumnoRepository;
import es.ggm.infor.softskills.dao.TotalSoftSkillRepository;
import es.ggm.infor.softskills.dto.AlumnoConTotalesDTO;
import es.ggm.infor.softskills.model.Alumno;
import es.ggm.infor.softskills.model.TotalSoftSkillPorAlumno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AlumnoResumenService {
    @Autowired
    private AlumnoRepository alumnoRepository;
    @Autowired private TotalSoftSkillRepository totalRepository;

    public List<AlumnoConTotalesDTO> obtenerResumenGeneral() {

            List<Alumno> alumnos = alumnoRepository.findAll();
            List<TotalSoftSkillPorAlumno> totales = totalRepository.findAll();

            Map<Long, AlumnoConTotalesDTO> resumenMap = new LinkedHashMap<>();

            for (Alumno alumno : alumnos) {
                AlumnoConTotalesDTO dto = new AlumnoConTotalesDTO();
                dto.setId(alumno.getId());
                dto.setNombre(alumno.getNombre());
                resumenMap.put(alumno.getId(), dto);
            }

            for (TotalSoftSkillPorAlumno total : totales) {
                AlumnoConTotalesDTO dto = resumenMap.get(total.getAlumno().getId());
                if (dto != null) {
                    dto.getTotalesPorSkill().put(
                            total.getSoftSkill().getNombre(),
                            total.getPuntuacionTotal()
                    );
                }
            }

            return new ArrayList<>(resumenMap.values());
        }
}