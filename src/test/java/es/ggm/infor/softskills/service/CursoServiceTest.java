package es.ggm.infor.softskills.service;

import es.ggm.infor.moodleintegration.client.IMoodleClient;
import es.ggm.infor.moodleintegration.dto.AlumnoMoodleDTO;
import es.ggm.infor.moodleintegration.dto.CursoMoodleDTO;
import es.ggm.infor.softskills.dao.CursoRepository;
import es.ggm.infor.softskills.dao.TotalSoftSkillPorAlumnoCursoRepository;
import es.ggm.infor.softskills.dto.mapper.AlumnoMapper;
import es.ggm.infor.softskills.dto.mapper.CursoMapper;
import es.ggm.infor.softskills.exception.GrupoNoResueltoException;
import es.ggm.infor.softskills.model.Alumno;
import es.ggm.infor.softskills.model.Curso;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CursoServiceTest {

    @Test
    void registrarCursoRechazaCursosSinIdNumberRegistrable() throws Exception {
        CursoRepository cursoRepository = mock(CursoRepository.class);
        CursoMapper cursoMapper = mock(CursoMapper.class);
        IMoodleClient moodleClient = mock(IMoodleClient.class);
        IAlumnoService alumnoService = mock(IAlumnoService.class);
        ISoftSkillService softSkillService = mock(ISoftSkillService.class);
        AlumnoMapper alumnoMapper = mock(AlumnoMapper.class);
        TotalSoftSkillPorAlumnoCursoRepository totalRepository = mock(TotalSoftSkillPorAlumnoCursoRepository.class);
        GrupoService grupoService = new GrupoService(mock(es.ggm.infor.softskills.dao.GrupoRepository.class));
        TotalesPorDefectoService totalesPorDefectoService = mock(TotalesPorDefectoService.class);

        CursoService service = new CursoService(
                cursoRepository,
                cursoMapper,
                moodleClient,
                alumnoService,
                softSkillService,
                alumnoMapper,
                totalRepository,
                grupoService,
                totalesPorDefectoService
        );

        CursoMoodleDTO dto = new CursoMoodleDTO();
        dto.id = 10L;
        dto.idnumber = "BAD";

        when(cursoRepository.existsById(10L)).thenReturn(false);
        when(moodleClient.getInfoCurso("token", 10L)).thenReturn(dto);
        doAnswer(invocation -> {
            var curso = (es.ggm.infor.softskills.model.Curso) invocation.getArgument(1);
            curso.setId(dto.id);
            return null;
        }).when(cursoMapper).updateFromDto(eq(dto), org.mockito.ArgumentMatchers.any());
        doAnswer(invocation -> {
            var curso = (es.ggm.infor.softskills.model.Curso) invocation.getArgument(1);
            curso.setIdNumber(invocation.getArgument(0));
            return null;
        }).when(cursoMapper).aplicarIdNumberEnCurso(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any());

        assertThrows(GrupoNoResueltoException.class, () -> service.registrarCurso("token", 10L, 99L));

        verify(moodleClient, never()).getAlumnos("token", 10L);
        verify(cursoRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void obtenerCursoConAlumnosSeparaNombreApellidosYOrdenaPorApellidos() throws Exception {
        CursoRepository cursoRepository = mock(CursoRepository.class);
        CursoMapper cursoMapper = mock(CursoMapper.class);
        IMoodleClient moodleClient = mock(IMoodleClient.class);
        IAlumnoService alumnoService = mock(IAlumnoService.class);
        ISoftSkillService softSkillService = mock(ISoftSkillService.class);
        AlumnoMapper alumnoMapper = Mappers.getMapper(AlumnoMapper.class);
        TotalSoftSkillPorAlumnoCursoRepository totalRepository = mock(TotalSoftSkillPorAlumnoCursoRepository.class);
        GrupoService grupoService = new GrupoService(mock(es.ggm.infor.softskills.dao.GrupoRepository.class));
        TotalesPorDefectoService totalesPorDefectoService = mock(TotalesPorDefectoService.class);

        CursoService service = new CursoService(
                cursoRepository,
                cursoMapper,
                moodleClient,
                alumnoService,
                softSkillService,
                alumnoMapper,
                totalRepository,
                grupoService,
                totalesPorDefectoService
        );

        Curso curso = Curso.builder()
                .id(10L)
                .alumnos(new ArrayList<>(List.of(
                        Alumno.builder().id(1L).build(),
                        Alumno.builder().id(2L).build(),
                        Alumno.builder().id(3L).build()
                )))
                .build();

        CursoMoodleDTO cursoMoodle = new CursoMoodleDTO();
        cursoMoodle.id = 10L;
        cursoMoodle.idnumber = "1_DAW_A_2526";

        when(cursoRepository.findById(10L)).thenReturn(Optional.of(curso));
        when(moodleClient.getInfoCurso("token", 10L)).thenReturn(cursoMoodle);
        when(moodleClient.getAlumnos("token", 10L)).thenReturn(List.of(
                alumnoMoodle(1L, "Ana", "Zamora"),
                alumnoMoodle(2L, "Luis", "Alonso"),
                alumnoMoodle(3L, "Bea", "Alonso")
        ));
        when(totalRepository.findByCursoIdAndAlumnoIdIn(eq(10L), org.mockito.ArgumentMatchers.anyList()))
                .thenReturn(List.of());

        Curso resultado = service.obtenerCursoConAlumnos("token", 10L);

        assertEquals(List.of("Bea", "Luis", "Ana"),
                resultado.getAlumnos().stream().map(Alumno::getNombre).toList());
        assertEquals(List.of("Alonso", "Alonso", "Zamora"),
                resultado.getAlumnos().stream().map(Alumno::getApellidos).toList());
        assertEquals(List.of("Bea Alonso", "Luis Alonso", "Ana Zamora"),
                resultado.getAlumnos().stream().map(Alumno::getNombreCompleto).toList());
    }

    private AlumnoMoodleDTO alumnoMoodle(Long id, String firstname, String lastname) {
        AlumnoMoodleDTO dto = new AlumnoMoodleDTO();
        dto.id = id;
        dto.firstname = firstname;
        dto.lastname = lastname;
        dto.fullname = firstname + " " + lastname;
        return dto;
    }
}
