package es.ggm.infor.softskills.service;

import es.ggm.infor.softskills.dao.GrupoRepository;
import es.ggm.infor.softskills.model.Curso;
import es.ggm.infor.softskills.model.Grupo;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GrupoServiceTest {

    @Test
    void puedeRegistrarseEnSoftSkillsEsFalseSiFaltaFormatoMinimo() {
        GrupoRepository grupoRepository = mock(GrupoRepository.class);
        GrupoService service = new GrupoService(grupoRepository);
        Curso curso = Curso.builder().idNumber("DAW_1").build();

        boolean registrable = service.puedeRegistrarseEnSoftSkills(curso);

        assertFalse(registrable);
        assertFalse(curso.isRegistrableEnSoftSkills());
    }

    @Test
    void puedeRegistrarseEnSoftSkillsEsFalseSiHaySegmentosVacios() {
        GrupoRepository grupoRepository = mock(GrupoRepository.class);
        GrupoService service = new GrupoService(grupoRepository);
        Curso curso = Curso.builder().idNumber("1__A_2425").build();

        boolean registrable = service.puedeRegistrarseEnSoftSkills(curso);

        assertFalse(registrable);
        assertFalse(curso.isRegistrableEnSoftSkills());
    }

    @Test
    void resolverGrupoDesdeCursoMarcaCursoComoRegistrableCuandoFormatoEsValido() {
        GrupoRepository grupoRepository = mock(GrupoRepository.class);
        GrupoService service = new GrupoService(grupoRepository);
        Curso curso = Curso.builder().idNumber("1_DAW_A_2425").build();
        Grupo grupo = Grupo.builder()
                .nivel("1")
                .cicloFormativo("DAW")
                .grupo("A")
                .cursoEscolar("24-25")
                .build();

        when(grupoRepository.findByNivelAndCicloFormativoAndGrupoAndCursoEscolar("1", "DAW", "A", "24-25"))
                .thenReturn(Optional.empty());
        when(grupoRepository.save(any(Grupo.class))).thenReturn(grupo);

        Grupo resuelto = service.resolverGrupoDesdeCurso(curso);

        assertNotNull(resuelto);
        assertTrue(curso.isRegistrableEnSoftSkills());
        verify(grupoRepository).save(any(Grupo.class));
    }
}
