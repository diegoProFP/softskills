package es.ggm.infor.softskills.service;

import es.ggm.infor.softskills.dao.ProfesorRepository;
import es.ggm.infor.softskills.model.Profesor;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProfesorServiceTest {

    @Test
    void crearProfesorGuardaSiNoExiste() {
        ProfesorRepository profesorRepository = mock(ProfesorRepository.class);
        ProfesorService service = new ProfesorService(profesorRepository);
        Profesor profesor = Profesor.builder().id(100L).administrador(false).build();

        when(profesorRepository.existsById(100L)).thenReturn(false);
        when(profesorRepository.save(profesor)).thenReturn(profesor);

        Profesor creado = service.crearProfesor(profesor);

        assertEquals(100L, creado.getId());
        verify(profesorRepository).save(profesor);
    }

    @Test
    void crearProfesorRechazaDuplicados() {
        ProfesorRepository profesorRepository = mock(ProfesorRepository.class);
        ProfesorService service = new ProfesorService(profesorRepository);
        Profesor profesor = Profesor.builder().id(100L).administrador(false).build();

        when(profesorRepository.existsById(100L)).thenReturn(true);

        assertThrows(EntityExistsException.class, () -> service.crearProfesor(profesor));
        verify(profesorRepository, never()).save(profesor);
    }

    @Test
    void actualizarProfesorCambiaMarcaAdministrador() {
        ProfesorRepository profesorRepository = mock(ProfesorRepository.class);
        ProfesorService service = new ProfesorService(profesorRepository);
        Profesor profesor = Profesor.builder().id(100L).administrador(false).build();

        when(profesorRepository.findById(100L)).thenReturn(Optional.of(profesor));
        when(profesorRepository.save(profesor)).thenReturn(profesor);

        Profesor actualizado = service.actualizarProfesor(100L, true);

        assertTrue(actualizado.isAdministrador());
        verify(profesorRepository).save(profesor);
    }

    @Test
    void actualizarProfesorFallaSiNoExiste() {
        ProfesorRepository profesorRepository = mock(ProfesorRepository.class);
        ProfesorService service = new ProfesorService(profesorRepository);

        when(profesorRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.actualizarProfesor(100L, true));
    }
}
