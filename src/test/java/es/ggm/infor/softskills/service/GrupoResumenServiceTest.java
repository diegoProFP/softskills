package es.ggm.infor.softskills.service;

import es.ggm.infor.moodleintegration.client.IMoodleClient;
import es.ggm.infor.moodleintegration.dto.AlumnoMoodleDTO;
import es.ggm.infor.softskills.dao.CursoRepository;
import es.ggm.infor.softskills.dao.GrupoRepository;
import es.ggm.infor.softskills.dao.SoftSkillRepository;
import es.ggm.infor.softskills.dao.TotalSoftSkillPorAlumnoGrupoRepository;
import es.ggm.infor.softskills.dto.AlumnoConTotalesDTO;
import es.ggm.infor.softskills.model.Curso;
import es.ggm.infor.softskills.model.Grupo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GrupoResumenServiceTest {

    @Test
    void obtenerResumenGrupoUsaCursoMoodleGrupoConfigurado() throws Exception {
        GrupoRepository grupoRepository = mock(GrupoRepository.class);
        TotalSoftSkillPorAlumnoGrupoRepository totalRepository = mock(TotalSoftSkillPorAlumnoGrupoRepository.class);
        CursoRepository cursoRepository = mock(CursoRepository.class);
        SoftSkillRepository softSkillRepository = mock(SoftSkillRepository.class);
        IMoodleClient moodleClient = mock(IMoodleClient.class);
        TotalesPorDefectoService totalesPorDefectoService = mock(TotalesPorDefectoService.class);
        RankingCalculationService rankingCalculationService = mock(RankingCalculationService.class);

        Grupo grupo = grupo(1L, 99L);
        when(grupoRepository.findByNivelAndCicloFormativoAndGrupoAndCursoEscolar("1", "DAW", "A", "25-26"))
                .thenReturn(Optional.of(grupo));
        when(totalRepository.findByGrupo(grupo)).thenReturn(List.of());
        when(softSkillRepository.findByGrupoId(1L)).thenReturn(List.of());
        when(moodleClient.getAlumnos("token", 99L)).thenReturn(List.of(alumnoMoodle(10L, "Ana Ruiz")));
        when(totalesPorDefectoService.construirTotales(anyCollection(), any())).thenReturn(List.of());
        when(rankingCalculationService.sumarMuestras(any())).thenReturn(0L);
        when(rankingCalculationService.calcularRankingScore(anyCollection(), any(), any())).thenReturn(BigDecimal.ZERO);

        GrupoResumenService service = new GrupoResumenService(
                grupoRepository,
                totalRepository,
                cursoRepository,
                softSkillRepository,
                moodleClient,
                totalesPorDefectoService,
                rankingCalculationService
        );

        List<AlumnoConTotalesDTO> resultado = service.obtenerResumenGrupo("token", "1", "DAW", "A", "25-26");

        assertEquals(1, resultado.size());
        assertEquals(10L, resultado.getFirst().getId());
        assertEquals("Ana Ruiz", resultado.getFirst().getNombre());
        verify(moodleClient).getAlumnos("token", 99L);
        verify(cursoRepository, never()).findFirstByGrupoAcademico_Id(1L);
    }

    @Test
    void obtenerResumenGrupoMantieneFallbackSiNoHayCursoMoodleGrupoConfigurado() throws Exception {
        GrupoRepository grupoRepository = mock(GrupoRepository.class);
        TotalSoftSkillPorAlumnoGrupoRepository totalRepository = mock(TotalSoftSkillPorAlumnoGrupoRepository.class);
        CursoRepository cursoRepository = mock(CursoRepository.class);
        SoftSkillRepository softSkillRepository = mock(SoftSkillRepository.class);
        IMoodleClient moodleClient = mock(IMoodleClient.class);
        TotalesPorDefectoService totalesPorDefectoService = mock(TotalesPorDefectoService.class);
        RankingCalculationService rankingCalculationService = mock(RankingCalculationService.class);

        Grupo grupo = grupo(1L, null);
        when(grupoRepository.findByNivelAndCicloFormativoAndGrupoAndCursoEscolar("1", "DAW", "A", "25-26"))
                .thenReturn(Optional.of(grupo));
        when(totalRepository.findByGrupo(grupo)).thenReturn(List.of());
        when(softSkillRepository.findByGrupoId(1L)).thenReturn(List.of());
        when(cursoRepository.findFirstByGrupoAcademico_Id(1L)).thenReturn(Optional.of(Curso.builder().id(55L).build()));
        when(moodleClient.getAlumnos("token", 55L)).thenReturn(List.of(alumnoMoodle(10L, "Ana Ruiz")));
        when(totalesPorDefectoService.construirTotales(anyCollection(), any())).thenReturn(List.of());
        when(rankingCalculationService.sumarMuestras(any())).thenReturn(0L);
        when(rankingCalculationService.calcularRankingScore(anyCollection(), any(), any())).thenReturn(BigDecimal.ZERO);

        GrupoResumenService service = new GrupoResumenService(
                grupoRepository,
                totalRepository,
                cursoRepository,
                softSkillRepository,
                moodleClient,
                totalesPorDefectoService,
                rankingCalculationService
        );

        service.obtenerResumenGrupo("token", "1", "DAW", "A", "25-26");

        verify(cursoRepository).findFirstByGrupoAcademico_Id(1L);
        verify(moodleClient).getAlumnos("token", 55L);
    }

    private Grupo grupo(Long id, Long cursoMoodleGrupoId) {
        return Grupo.builder()
                .id(id)
                .nivel("1")
                .cicloFormativo("DAW")
                .grupo("A")
                .cursoEscolar("25-26")
                .cursoMoodleGrupoId(cursoMoodleGrupoId)
                .build();
    }

    private AlumnoMoodleDTO alumnoMoodle(Long id, String fullname) {
        AlumnoMoodleDTO dto = new AlumnoMoodleDTO();
        dto.id = id;
        dto.fullname = fullname;
        return dto;
    }
}
