package es.ggm.infor.softskills.controller;

import es.ggm.infor.softskills.dto.ProfesorDTO;
import es.ggm.infor.softskills.model.Profesor;
import es.ggm.infor.softskills.service.IProfesorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping(MainController.BASE_PATH + "/profesores")
@PreAuthorize("hasRole('ADMIN')")
public class ProfesoresController extends MainController {

    private final IProfesorService profesorService;

    public ProfesoresController(IProfesorService profesorService) {
        this.profesorService = profesorService;
    }

    @GetMapping
    public ResponseEntity<List<ProfesorDTO>> getProfesores() {
        List<ProfesorDTO> profesores = profesorService.getAllProfesores().stream()
                .sorted(Comparator.comparing(Profesor::getId))
                .map(this::toDto)
                .toList();

        return ResponseEntity.ok(profesores);
    }

    @PostMapping
    public ResponseEntity<ProfesorDTO> crearProfesor(@Valid @RequestBody ProfesorDTO request) {
        Profesor profesor = Profesor.builder()
                .id(request.getId())
                .administrador(request.isAdministrador())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toDto(profesorService.crearProfesor(profesor)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfesorDTO> actualizarProfesor(@PathVariable Long id,
                                                          @RequestBody ProfesorDTO request) {
        Profesor profesor = profesorService.actualizarProfesor(id, request.isAdministrador());
        return ResponseEntity.ok(toDto(profesor));
    }

    private ProfesorDTO toDto(Profesor profesor) {
        return ProfesorDTO.builder()
                .id(profesor.getId())
                .administrador(profesor.isAdministrador())
                .build();
    }
}
