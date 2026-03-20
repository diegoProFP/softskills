package es.ggm.infor.softskills.controller;


import es.ggm.infor.softskills.dto.AlumnoConTotalesDTO;
import es.ggm.infor.softskills.service.AlumnoDetalleService;
import es.ggm.infor.softskills.service.AlumnoResumenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping(MainController.BASE_PATH + "/alumnos")
public class AlumnoController extends MainController{
    @Autowired
    private AlumnoResumenService resumenService;
    @Autowired private AlumnoDetalleService detalleService;

    @GetMapping("/resumen")
    public List<AlumnoConTotalesDTO> getResumenGeneral() {
        return resumenService.obtenerResumenGeneral();
    }

    @GetMapping("/{id}")
    public AlumnoConTotalesDTO getAlumnoConTotales(@PathVariable Long id) {
        return detalleService.obtenerDetalleAlumno(id);
    }
}