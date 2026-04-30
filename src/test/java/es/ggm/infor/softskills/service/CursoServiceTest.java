package es.ggm.infor.softskills.service;

import es.ggm.infor.moodleintegration.client.IMoodleClient;
import es.ggm.infor.moodleintegration.dto.CursoMoodleDTO;
import es.ggm.infor.softskills.dao.CursoRepository;
import es.ggm.infor.softskills.dao.TotalSoftSkillPorAlumnoCursoRepository;
import es.ggm.infor.softskills.dto.mapper.AlumnoMapper;
import es.ggm.infor.softskills.dto.mapper.CursoMapper;
import es.ggm.infor.softskills.exception.GrupoNoResueltoException;
import org.junit.jupiter.api.Test;

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
}
