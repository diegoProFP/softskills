package es.ggm.infor.softskills.service;

import es.ggm.infor.softskills.dao.CursoRepository;
import es.ggm.infor.softskills.dao.MuestraSoftSkillRepository;
import es.ggm.infor.softskills.dao.MotivosSoftSkillRepository;
import es.ggm.infor.softskills.dao.SoftSkillRepository;
import es.ggm.infor.softskills.dto.AdminSoftSkillRequest;
import es.ggm.infor.softskills.dto.BorrarMuestraResponse;
import es.ggm.infor.softskills.dto.MuestraOperacionResponse;
import es.ggm.infor.softskills.dto.MotivoSoftSkillDTO;
import es.ggm.infor.softskills.dto.MuestraRequest;
import es.ggm.infor.softskills.dto.MuestraSoftSkillDetalleDTO;
import es.ggm.infor.softskills.dto.MuestrasCursoAlumnoSoftSkillDTO;
import es.ggm.infor.softskills.dto.SoftSkillResumenDTO;
import es.ggm.infor.softskills.dto.TotalActualizadoDTO;
import es.ggm.infor.softskills.model.Alumno;
import es.ggm.infor.softskills.model.Curso;
import es.ggm.infor.softskills.model.MuestraSoftSkill;
import es.ggm.infor.softskills.model.MotivosSoftSkill;
import es.ggm.infor.softskills.model.NivelMuestraSoftSkill;
import es.ggm.infor.softskills.model.Profesor;
import es.ggm.infor.softskills.model.SoftSkill;
import es.ggm.infor.softskills.model.TotalSoftSkillPorAlumnoCurso;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SoftSkillService implements ISoftSkillService {
    private static final Logger log = LoggerFactory.getLogger(SoftSkillService.class);

    private final SoftSkillRepository softSkillRepository;
    private final CursoRepository cursoRepository;
    private final IAlumnoService alumnoService;
    private final MuestraSoftSkillRepository muestraRepository;
    private final SoftSkillTotalService softSkillTotalService;
    private final MotivosSoftSkillRepository motivosSoftSkillRepository;

    @Override
    public List<SoftSkill> getAllSoftSkills() {
        return softSkillRepository.findAll();
    }

    @Override
    public SoftSkill getSoftSkillById(Long id) {
        return softSkillRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Soft Skill not found with id: " + id));
    }

    @Override
    @Transactional
    public void insertarMuestra(MuestraRequest request) {
        log.debug("Insertando muestra para curso {}, alumno {}, skill {}, valor {}",
                request.getCursoId(), request.getAlumnoId(), request.getSoftSkillId(), request.getValor());

        Curso curso = cursoRepository.findById(request.getCursoId())
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado: " + request.getCursoId()));

        Alumno alumno = alumnoService.getAlumnoById(request.getAlumnoId());

        SoftSkill softSkill = softSkillRepository.findById(request.getSoftSkillId())
                .orElseThrow(() -> new IllegalArgumentException("SoftSkill no encontrada: " + request.getSoftSkillId()));

        Optional<MotivosSoftSkill> motivoSeleccionado = resolverMotivoSeleccionado(request, softSkill);

        boolean pertenece = curso.getAlumnos().stream().anyMatch(a -> a.getId().equals(alumno.getId()));
        if (!pertenece) {
            log.warn("El alumno {} no pertenece al curso {}", alumno.getId(), curso.getId());
            throw new SecurityException("El alumno no pertenece al curso indicado");
        }

        Profesor profesor = Profesor.builder().id(request.getProfesorId()).build();
        NivelMuestraSoftSkill nivel = resolverNivel(request, motivoSeleccionado);
        int valor = resolverValor(request, motivoSeleccionado);
        validarValor(valor);

        MuestraSoftSkill muestra = MuestraSoftSkill.builder()
                .curso(curso)
                .alumno(alumno)
                .profesor(profesor)
                .softSkill(softSkill)
                .valor(valor)
                .nivel(nivel)
                .pesoNivel(nivel.getPeso())
                .motivoPredefinido(motivoSeleccionado.orElse(null))
                .motivo(resolverTextoMotivo(request, motivoSeleccionado))
                .motivoComentario(normalizarTexto(request.getMotivoComentario()))
                .fecha(LocalDateTime.now())
                .build();

        muestraRepository.save(muestra);
        softSkillTotalService.aplicarNuevaMuestra(muestra);
        log.info("Muestra registrada con exito: {}", muestra);
    }

    @Override
    public MuestrasCursoAlumnoSoftSkillDTO obtenerMuestrasPorCursoAlumnoSoftSkill(Long cursoId,
                                                                                  Long alumnoId,
                                                                                  Long softSkillId,
                                                                                  Long usuarioId,
                                                                                  boolean isAdmin) {
        Curso curso = obtenerCursoValidado(cursoId);
        Alumno alumno = alumnoService.getAlumnoById(alumnoId);
        SoftSkill softSkill = obtenerSoftSkillValidada(softSkillId);
        validarAlumnoEnCurso(curso, alumno);
        validarAccesoCurso(curso, usuarioId, isAdmin);

        List<MuestraSoftSkill> muestras = muestraRepository
                .findByCurso_IdAndAlumno_IdAndSoftSkill_IdOrderByFechaDesc(cursoId, alumnoId, softSkillId);

        MuestrasCursoAlumnoSoftSkillDTO dto = new MuestrasCursoAlumnoSoftSkillDTO();
        dto.setCursoId(curso.getId());
        dto.setCursoNombre(curso.getNombre());
        dto.setAlumnoId(alumno.getId());
        dto.setAlumnoNombre(alumno.getNombre());
        dto.setSoftSkill(construirSoftSkillResumen(softSkill));
        dto.setNumMuestras((long) muestras.size());
        dto.setMuestras(muestras.stream()
                .map(muestra -> construirMuestraDetalle(muestra, usuarioId, isAdmin))
                .toList());
        return dto;
    }

    @Override
    @Transactional
    public MuestraOperacionResponse actualizarMuestra(Long muestraId, MuestraRequest request, Long usuarioId, boolean isAdmin) {
        MuestraSoftSkill muestra = muestraRepository.findById(muestraId)
                .orElseThrow(() -> new EntityNotFoundException("Muestra no encontrada"));

        validarContextoObligatorio(request);
        validarContextoMuestra(muestra, request.getCursoId(), request.getAlumnoId(), request.getSoftSkillId());
        validarPuedeModificar(muestra, usuarioId, isAdmin, "editar");

        Optional<MotivosSoftSkill> motivoSeleccionado = resolverMotivoSeleccionado(request, muestra.getSoftSkill());
        NivelMuestraSoftSkill nivel = resolverNivel(request, motivoSeleccionado);
        int valor = resolverValor(request, motivoSeleccionado);
        validarValor(valor);

        muestra.setValor(valor);
        muestra.setNivel(nivel);
        muestra.setPesoNivel(nivel.getPeso());
        muestra.setMotivoPredefinido(motivoSeleccionado.orElse(null));
        muestra.setMotivo(resolverTextoMotivo(request, motivoSeleccionado));
        muestra.setMotivoComentario(normalizarTexto(request.getMotivoComentario()));

        MuestraSoftSkill guardada = muestraRepository.save(muestra);
        Optional<TotalSoftSkillPorAlumnoCurso> total = softSkillTotalService.recalcularTrasCambio(guardada);
        return construirOperacionResponse(guardada, total, usuarioId, isAdmin);
    }

    @Override
    @Transactional
    public BorrarMuestraResponse borrarMuestra(Long muestraId, Long cursoId, Long alumnoId, Long softSkillId,
                                               Long usuarioId, boolean isAdmin) {
        MuestraSoftSkill muestra = muestraRepository.findById(muestraId)
                .orElseThrow(() -> new EntityNotFoundException("Muestra no encontrada"));

        validarContextoMuestra(muestra, cursoId, alumnoId, softSkillId);
        validarPuedeModificar(muestra, usuarioId, isAdmin, "borrar");

        Long cursoBorradoId = muestra.getCurso().getId();
        Long alumnoBorradoId = muestra.getAlumno().getId();
        Long softSkillBorradaId = muestra.getSoftSkill().getId();

        muestraRepository.delete(muestra);
        Optional<TotalSoftSkillPorAlumnoCurso> total = softSkillTotalService.recalcularTrasCambio(muestra);

        BorrarMuestraResponse response = new BorrarMuestraResponse();
        response.setDeleted(true);
        response.setMuestraId(muestraId);
        response.setCursoId(cursoBorradoId);
        response.setAlumnoId(alumnoBorradoId);
        response.setSoftSkillId(softSkillBorradaId);
        response.setTotalActualizado(construirTotalActualizado(alumnoBorradoId, softSkillBorradaId, total));
        return response;
    }

    private Optional<MotivosSoftSkill> resolverMotivoSeleccionado(MuestraRequest request, SoftSkill softSkill) {
        if (request.getMotivoId() == null) {
            return Optional.empty();
        }

        MotivosSoftSkill motivo = motivosSoftSkillRepository.findById(request.getMotivoId())
                .orElseThrow(() -> new IllegalArgumentException("Motivo no encontrado: " + request.getMotivoId()));
        if (motivo.getSoftSkill() == null || !Objects.equals(motivo.getSoftSkill().getId(), softSkill.getId())) {
            throw new IllegalArgumentException("El motivo " + request.getMotivoId()
                    + " no pertenece a la soft skill " + softSkill.getId() + ".");
        }
        return Optional.of(motivo);
    }

    private NivelMuestraSoftSkill resolverNivel(MuestraRequest request, Optional<MotivosSoftSkill> motivoSeleccionado) {
        return motivoSeleccionado
                .map(MotivosSoftSkill::getNivelPorDefecto)
                .orElse(request.getNivel() != null ? request.getNivel() : NivelMuestraSoftSkill.NORMAL);
    }

    private int resolverValor(MuestraRequest request, Optional<MotivosSoftSkill> motivoSeleccionado) {
        return motivoSeleccionado
                .map(MotivosSoftSkill::getValorPorDefecto)
                .orElse(request.getValor());
    }

    private void validarValor(int valor) {
        if (valor != 1 && valor != -1) {
            throw new IllegalArgumentException("El valor de la muestra debe ser 1 o -1.");
        }
    }

    private String resolverTextoMotivo(MuestraRequest request, Optional<MotivosSoftSkill> motivoSeleccionado) {
        String motivoLibre = normalizarMotivo(request.getMotivo());
        if (motivoLibre != null) {
            return motivoLibre;
        }
        return motivoSeleccionado
                .map(MotivosSoftSkill::getMotivo)
                .map(this::normalizarMotivo)
                .orElse(null);
    }

    private Curso obtenerCursoValidado(Long cursoId) {
        if (cursoId == null) {
            throw new IllegalArgumentException("El curso es obligatorio.");
        }
        return cursoRepository.findById(cursoId)
                .orElseThrow(() -> new EntityNotFoundException("Curso no encontrado"));
    }

    private SoftSkill obtenerSoftSkillValidada(Long softSkillId) {
        if (softSkillId == null) {
            throw new IllegalArgumentException("La soft skill es obligatoria.");
        }
        return softSkillRepository.findById(softSkillId)
                .orElseThrow(() -> new EntityNotFoundException("Soft skill no encontrada"));
    }

    private void validarAlumnoEnCurso(Curso curso, Alumno alumno) {
        boolean pertenece = curso.getAlumnos() != null
                && curso.getAlumnos().stream().anyMatch(a -> Objects.equals(a.getId(), alumno.getId()));
        if (!pertenece) {
            throw new IllegalArgumentException("El alumno no pertenece al curso indicado.");
        }
    }

    private void validarAccesoCurso(Curso curso, Long usuarioId, boolean isAdmin) {
        if (isAdmin) {
            return;
        }
        Long profesorId = curso.getProfesor() != null ? curso.getProfesor().getId() : null;
        if (!Objects.equals(profesorId, usuarioId)) {
            throw new AccessDeniedException("No puedes consultar muestras de un curso de otro profesor.");
        }
    }

    private void validarPuedeModificar(MuestraSoftSkill muestra, Long usuarioId, boolean isAdmin, String accion) {
        if (isAdmin) {
            return;
        }
        Long profesorId = muestra.getProfesor() != null ? muestra.getProfesor().getId() : null;
        if (!Objects.equals(profesorId, usuarioId)) {
            throw new AccessDeniedException("No puedes " + accion + " una muestra registrada por otro profesor.");
        }
    }

    private void validarContextoMuestra(MuestraSoftSkill muestra, Long cursoId, Long alumnoId, Long softSkillId) {
        if (cursoId != null && !Objects.equals(muestra.getCurso().getId(), cursoId)) {
            throw new IllegalArgumentException("La muestra no pertenece al curso indicado.");
        }
        if (alumnoId != null && !Objects.equals(muestra.getAlumno().getId(), alumnoId)) {
            throw new IllegalArgumentException("La muestra no pertenece al alumno indicado.");
        }
        if (softSkillId != null && !Objects.equals(muestra.getSoftSkill().getId(), softSkillId)) {
            throw new IllegalArgumentException("La muestra no pertenece a la soft skill indicada.");
        }
    }

    private void validarContextoObligatorio(MuestraRequest request) {
        if (request.getCursoId() == null || request.getAlumnoId() == null || request.getSoftSkillId() == null) {
            throw new IllegalArgumentException("Curso, alumno y soft skill son obligatorios para modificar una muestra.");
        }
    }

    private MuestraOperacionResponse construirOperacionResponse(MuestraSoftSkill muestra,
                                                                Optional<TotalSoftSkillPorAlumnoCurso> total,
                                                                Long usuarioId,
                                                                boolean isAdmin) {
        MuestraOperacionResponse response = new MuestraOperacionResponse();
        response.setMuestra(construirMuestraDetalle(muestra, usuarioId, isAdmin));
        response.setTotalActualizado(construirTotalActualizado(
                muestra.getAlumno().getId(),
                muestra.getSoftSkill().getId(),
                total
        ));
        return response;
    }

    private TotalActualizadoDTO construirTotalActualizado(Long alumnoId,
                                                          Long softSkillId,
                                                          Optional<TotalSoftSkillPorAlumnoCurso> total) {
        TotalActualizadoDTO dto = new TotalActualizadoDTO();
        dto.setAlumnoId(alumnoId);
        dto.setSoftSkillId(softSkillId);
        dto.setPuntuacionTotal(total.map(TotalSoftSkillPorAlumnoCurso::getPuntuacionTotal).orElse(null));
        dto.setNumMuestras(total.map(TotalSoftSkillPorAlumnoCurso::getNumMuestras).orElse(0L));
        return dto;
    }

    private MuestraSoftSkillDetalleDTO construirMuestraDetalle(MuestraSoftSkill muestra, Long usuarioId, boolean isAdmin) {
        boolean puedeModificar = isAdmin || Objects.equals(
                muestra.getProfesor() != null ? muestra.getProfesor().getId() : null,
                usuarioId
        );

        MuestraSoftSkillDetalleDTO dto = new MuestraSoftSkillDetalleDTO();
        dto.setId(muestra.getId());
        dto.setFecha(muestra.getFecha());
        dto.setValor(muestra.getValor());
        dto.setNivel(muestra.getNivel() != null ? muestra.getNivel().name() : null);
        dto.setPesoNivel(muestra.getPesoNivel());
        dto.setMotivoId(muestra.getMotivoPredefinido() != null ? muestra.getMotivoPredefinido().getId() : null);
        dto.setMotivo(muestra.getMotivo());
        dto.setMotivoComentario(muestra.getMotivoComentario());
        dto.setProfesorId(muestra.getProfesor() != null ? muestra.getProfesor().getId() : null);
        dto.setEditable(puedeModificar);
        dto.setDeletable(puedeModificar);
        return dto;
    }

    private SoftSkillResumenDTO construirSoftSkillResumen(SoftSkill softSkill) {
        SoftSkillResumenDTO dto = new SoftSkillResumenDTO();
        dto.setId(softSkill.getId());
        dto.setCodigo(softSkill.getCodigo() != null ? softSkill.getCodigo().name() : null);
        dto.setNombre(softSkill.getNombre());
        dto.setDescripcion(softSkill.getDescripcion());
        dto.setTipoMedicion(softSkill.getTipoMedicion() != null ? softSkill.getTipoMedicion().name() : null);
        return dto;
    }

    @Override
    public List<SoftSkill> getSoftSkillsByCursoId(Long cursoId) {
        return softSkillRepository.findByCursoId(cursoId);
    }

    @Override
    @Transactional
    public synchronized SoftSkill actualizarSoftSkill(Long id, AdminSoftSkillRequest request) {
        SoftSkill softSkill = softSkillRepository.findByIdWithMotivos(id)
                .orElseThrow(() -> new EntityNotFoundException("Soft Skill not found with id: " + id));

        softSkill.setNombre(request.getNombre().trim());
        softSkill.setDescripcion(normalizarTexto(request.getDescripcion()));
        softSkill.setTipoMedicion(request.getTipoMedicion());
        softSkill.setCodigo(request.getCodigo());
        sincronizarMotivos(softSkill, request.getListaMotivos());

        return softSkillRepository.save(softSkill);
    }

    private String normalizarMotivo(String motivo) {
        if (motivo == null) {
            return null;
        }
        String motivoNormalizado = motivo.trim();
        return motivoNormalizado.isEmpty() ? null : motivoNormalizado;
    }

    private String normalizarTexto(String texto) {
        if (texto == null) {
            return null;
        }
        String textoNormalizado = texto.trim();
        return textoNormalizado.isEmpty() ? null : textoNormalizado;
    }

    private void sincronizarMotivos(SoftSkill softSkill, List<MotivoSoftSkillDTO> motivosRequest) {
        List<MotivosSoftSkill> motivosActuales = softSkill.getListaMotivos();
        if (motivosActuales == null) {
            motivosActuales = new ArrayList<>();
            softSkill.setListaMotivos(motivosActuales);
        }

        Map<Long, MotivosSoftSkill> motivosPorId = motivosActuales.stream()
                .filter(motivo -> motivo.getId() != null)
                .collect(Collectors.toMap(MotivosSoftSkill::getId, Function.identity()));

        Set<Long> motivosConservados = new HashSet<>();
        AtomicLong siguienteId = new AtomicLong(motivosSoftSkillRepository.findMaxId().orElse(0L) + 1);
        if (motivosRequest != null) {
            for (MotivoSoftSkillDTO motivoRequest : motivosRequest) {
                String textoMotivo = normalizarTexto(motivoRequest.getMotivo());
                if (textoMotivo == null) {
                    throw new IllegalArgumentException("El motivo no puede estar vacio.");
                }

                MotivosSoftSkill motivo = resolverMotivo(softSkill, motivosPorId, motivoRequest);
                motivo.setMotivo(textoMotivo);
                motivo.setDescripcionCorta(normalizarTexto(motivoRequest.getDescripcionCorta()));
                motivo.setDescripcionLarga(normalizarTexto(motivoRequest.getDescripcionLarga()));
                motivo.setValorPorDefecto(motivoRequest.getValorPorDefecto());
                motivo.setNivelPorDefecto(motivoRequest.getNivelPorDefecto());
                motivo.setSoftSkill(softSkill);
                if (motivo.getId() == null) {
                    motivo.setId(siguienteId.getAndIncrement());
                    motivosConservados.add(motivo.getId());
                    motivosActuales.add(motivo);
                } else {
                    if (!motivosConservados.add(motivo.getId())) {
                        throw new IllegalArgumentException("El motivo " + motivo.getId() + " esta duplicado en la solicitud.");
                    }
                }
            }
        }

        motivosActuales.removeIf(motivo -> motivo.getId() != null && !motivosConservados.contains(motivo.getId()));
        motivosActuales.sort(Comparator.comparing(MotivosSoftSkill::getMotivo, String.CASE_INSENSITIVE_ORDER));
    }

    private MotivosSoftSkill resolverMotivo(SoftSkill softSkill,
                                            Map<Long, MotivosSoftSkill> motivosPorId,
                                            MotivoSoftSkillDTO motivoRequest) {
        Long motivoId = motivoRequest.getId();
        if (motivoId == null) {
            return MotivosSoftSkill.builder()
                    .softSkill(softSkill)
                    .build();
        }

        MotivosSoftSkill motivo = motivosPorId.get(motivoId);
        if (motivo == null) {
            throw new IllegalArgumentException("El motivo " + motivoId + " no pertenece a la soft skill " + softSkill.getId() + ".");
        }
        return motivo;
    }
}
