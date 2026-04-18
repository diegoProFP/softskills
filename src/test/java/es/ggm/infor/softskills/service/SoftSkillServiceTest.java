package es.ggm.infor.softskills.service;

import es.ggm.infor.softskills.dao.CursoRepository;
import es.ggm.infor.softskills.dao.MuestraSoftSkillRepository;
import es.ggm.infor.softskills.dao.SoftSkillRepository;
import es.ggm.infor.softskills.dto.MuestraRequest;
import es.ggm.infor.softskills.model.Alumno;
import es.ggm.infor.softskills.model.Curso;
import es.ggm.infor.softskills.model.MuestraSoftSkill;
import es.ggm.infor.softskills.model.NivelMuestraSoftSkill;
import es.ggm.infor.softskills.model.SoftSkill;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SoftSkillServiceTest {

    @Test
    void insertarMuestraCopiaElTextoDelMotivoEnLaMuestra() {
        SoftSkillRepository softSkillRepository = mock(SoftSkillRepository.class);
        CursoRepository cursoRepository = mock(CursoRepository.class);
        IAlumnoService alumnoService = mock(IAlumnoService.class);
        MuestraSoftSkillRepository muestraRepository = mock(MuestraSoftSkillRepository.class);
        SoftSkillTotalService softSkillTotalService = mock(SoftSkillTotalService.class);
        SoftSkillService service = new SoftSkillService(
                softSkillRepository,
                cursoRepository,
                alumnoService,
                muestraRepository,
                softSkillTotalService
        );

        Alumno alumno = Alumno.builder().id(10L).build();
        Curso curso = Curso.builder().id(20L).alumnos(List.of(alumno)).build();
        SoftSkill softSkill = SoftSkill.builder().id(30L).build();
        MuestraRequest request = new MuestraRequest(
                40L,
                curso.getId(),
                alumno.getId(),
                softSkill.getId(),
                -1,
                NivelMuestraSoftSkill.NORMAL,
                "  Interrumpe al equipo  "
        );

        when(cursoRepository.findById(curso.getId())).thenReturn(Optional.of(curso));
        when(alumnoService.getAlumnoById(alumno.getId())).thenReturn(alumno);
        when(softSkillRepository.findById(softSkill.getId())).thenReturn(Optional.of(softSkill));
        when(muestraRepository.save(any(MuestraSoftSkill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.insertarMuestra(request);

        ArgumentCaptor<MuestraSoftSkill> captor = ArgumentCaptor.forClass(MuestraSoftSkill.class);
        verify(muestraRepository).save(captor.capture());
        verify(softSkillTotalService).aplicarNuevaMuestra(captor.getValue());
        assertEquals("Interrumpe al equipo", captor.getValue().getMotivo());
    }
}
