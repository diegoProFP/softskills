package es.ggm.infor.softskills.service;

import es.ggm.infor.softskills.dao.CursoRepository;
import es.ggm.infor.softskills.dao.MuestraSoftSkillRepository;
import es.ggm.infor.softskills.dao.MotivosSoftSkillRepository;
import es.ggm.infor.softskills.dao.SoftSkillRepository;
import es.ggm.infor.softskills.dto.AdminSoftSkillRequest;
import es.ggm.infor.softskills.dto.MotivoSoftSkillDTO;
import es.ggm.infor.softskills.dto.MuestraRequest;
import es.ggm.infor.softskills.model.Alumno;
import es.ggm.infor.softskills.model.CodigoSoftSkill;
import es.ggm.infor.softskills.model.Curso;
import es.ggm.infor.softskills.model.MuestraSoftSkill;
import es.ggm.infor.softskills.model.MotivosSoftSkill;
import es.ggm.infor.softskills.model.NivelMuestraSoftSkill;
import es.ggm.infor.softskills.model.SoftSkill;
import es.ggm.infor.softskills.model.TipoMedicionSoftSkill;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
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
                softSkillTotalService,
                mock(MotivosSoftSkillRepository.class)
        );

        Alumno alumno = Alumno.builder().id(10L).build();
        Curso curso = Curso.builder().id(20L).alumnos(List.of(alumno)).build();
        SoftSkill softSkill = SoftSkill.builder().id(30L).build();
        MuestraRequest request = new MuestraRequest(
                40L,
                curso.getId(),
                alumno.getId(),
                softSkill.getId(),
                null,
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

    @Test
    void insertarMuestraUsaDefaultsDelMotivoSeleccionado() {
        SoftSkillRepository softSkillRepository = mock(SoftSkillRepository.class);
        CursoRepository cursoRepository = mock(CursoRepository.class);
        IAlumnoService alumnoService = mock(IAlumnoService.class);
        MuestraSoftSkillRepository muestraRepository = mock(MuestraSoftSkillRepository.class);
        SoftSkillTotalService softSkillTotalService = mock(SoftSkillTotalService.class);
        MotivosSoftSkillRepository motivosSoftSkillRepository = mock(MotivosSoftSkillRepository.class);
        SoftSkillService service = new SoftSkillService(
                softSkillRepository,
                cursoRepository,
                alumnoService,
                muestraRepository,
                softSkillTotalService,
                motivosSoftSkillRepository
        );

        Alumno alumno = Alumno.builder().id(10L).build();
        Curso curso = Curso.builder().id(20L).alumnos(List.of(alumno)).build();
        SoftSkill softSkill = SoftSkill.builder().id(30L).build();
        MotivosSoftSkill motivo = MotivosSoftSkill.builder()
                .id(50L)
                .motivo("Bloqueo pasivo prolongado")
                .valorPorDefecto(-1)
                .nivelPorDefecto(NivelMuestraSoftSkill.NORMAL)
                .softSkill(softSkill)
                .build();
        MuestraRequest request = new MuestraRequest(
                40L,
                curso.getId(),
                alumno.getId(),
                softSkill.getId(),
                motivo.getId(),
                0,
                null,
                null
        );

        when(cursoRepository.findById(curso.getId())).thenReturn(Optional.of(curso));
        when(alumnoService.getAlumnoById(alumno.getId())).thenReturn(alumno);
        when(softSkillRepository.findById(softSkill.getId())).thenReturn(Optional.of(softSkill));
        when(motivosSoftSkillRepository.findById(motivo.getId())).thenReturn(Optional.of(motivo));
        when(muestraRepository.save(any(MuestraSoftSkill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.insertarMuestra(request);

        ArgumentCaptor<MuestraSoftSkill> captor = ArgumentCaptor.forClass(MuestraSoftSkill.class);
        verify(muestraRepository).save(captor.capture());
        assertEquals("Bloqueo pasivo prolongado", captor.getValue().getMotivo());
        assertEquals(-1, captor.getValue().getValor());
        assertEquals(NivelMuestraSoftSkill.NORMAL, captor.getValue().getNivel());
    }

    @Test
    void actualizarSoftSkillSincronizaDatosYMotivos() {
        SoftSkillRepository softSkillRepository = mock(SoftSkillRepository.class);
        MotivosSoftSkillRepository motivosSoftSkillRepository = mock(MotivosSoftSkillRepository.class);
        SoftSkillService service = new SoftSkillService(
                softSkillRepository,
                mock(CursoRepository.class),
                mock(IAlumnoService.class),
                mock(MuestraSoftSkillRepository.class),
                mock(SoftSkillTotalService.class),
                motivosSoftSkillRepository
        );

        SoftSkill softSkill = SoftSkill.builder()
                .id(30L)
                .nombre("Anterior")
                .descripcion("Descripcion anterior")
                .tipoMedicion(TipoMedicionSoftSkill.PENALIZACION_POR_TRAMOS)
                .codigo(CodigoSoftSkill.GENERICA)
                .listaMotivos(new ArrayList<>())
                .build();
        softSkill.getListaMotivos().add(MotivosSoftSkill.builder()
                .id(1L)
                .motivo("Motivo antiguo")
                .softSkill(softSkill)
                .build());
        softSkill.getListaMotivos().add(MotivosSoftSkill.builder()
                .id(2L)
                .motivo("Motivo a eliminar")
                .softSkill(softSkill)
                .build());

        AdminSoftSkillRequest request = AdminSoftSkillRequest.builder()
                .nombre(" Participacion ")
                .descripcion("  Suma participaciones positivas  ")
                .tipoMedicion(TipoMedicionSoftSkill.ACUMULACION_SATURADA)
                .codigo(CodigoSoftSkill.PARTICIPACION)
                .listaMotivos(List.of(
                        MotivoSoftSkillDTO.builder().id(1L).motivo("  Ayuda al equipo  ").build(),
                        MotivoSoftSkillDTO.builder()
                                .motivo("Propone soluciones")
                                .descripcionCorta("Propone")
                                .descripcionLarga("Propone soluciones utiles para avanzar.")
                                .valorPorDefecto(1)
                                .nivelPorDefecto(NivelMuestraSoftSkill.NORMAL)
                                .build()
                ))
                .build();

        when(softSkillRepository.findByIdWithMotivos(softSkill.getId())).thenReturn(Optional.of(softSkill));
        when(motivosSoftSkillRepository.findMaxId()).thenReturn(Optional.of(2L));
        when(softSkillRepository.save(any(SoftSkill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SoftSkill resultado = service.actualizarSoftSkill(softSkill.getId(), request);

        assertEquals("Participacion", resultado.getNombre());
        assertEquals("Suma participaciones positivas", resultado.getDescripcion());
        assertEquals(TipoMedicionSoftSkill.ACUMULACION_SATURADA, resultado.getTipoMedicion());
        assertEquals(CodigoSoftSkill.PARTICIPACION, resultado.getCodigo());
        assertEquals(2, resultado.getListaMotivos().size());
        assertEquals(1L, resultado.getListaMotivos().get(0).getId());
        assertEquals("Ayuda al equipo", resultado.getListaMotivos().get(0).getMotivo());
        assertEquals(3L, resultado.getListaMotivos().get(1).getId());
        assertEquals("Propone soluciones", resultado.getListaMotivos().get(1).getMotivo());
        assertEquals("Propone", resultado.getListaMotivos().get(1).getDescripcionCorta());
        assertEquals("Propone soluciones utiles para avanzar.", resultado.getListaMotivos().get(1).getDescripcionLarga());
        assertEquals(1, resultado.getListaMotivos().get(1).getValorPorDefecto());
        assertEquals(NivelMuestraSoftSkill.NORMAL, resultado.getListaMotivos().get(1).getNivelPorDefecto());
        verify(softSkillRepository).save(softSkill);
    }
}
