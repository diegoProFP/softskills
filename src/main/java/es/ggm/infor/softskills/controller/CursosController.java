package es.ggm.infor.softskills.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(MainController.BASE_PATH + "/cursos")
public class CursosController extends MainController{

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String getAlumnos(@PathVariable String id) {
        return "Lista de alumnos del curso  " + id;
    }
}
