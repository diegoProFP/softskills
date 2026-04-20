package es.ggm.infor.softskills.service;

import es.ggm.infor.softskills.dao.CursoRepository;
import es.ggm.infor.softskills.dao.MuestraSoftSkillRepository;
import es.ggm.infor.softskills.dao.MotivosSoftSkillRepository;
import es.ggm.infor.softskills.dao.SoftSkillRepository;
import es.ggm.infor.softskills.dto.AdminSoftSkillRequest;
import es.ggm.infor.softskills.dto.MotivoSoftSkillDTO;
import es.ggm.infor.softskills.dto.MuestraRequest;
import es.ggm.infor.softskills.model.Alumno;
import es.ggm.infor.softskills.model.Curso;
import es.ggm.infor.softskills.model.MuestraSoftSkill;
import es.ggm.infor.softskills.model.MotivosSoftSkill;
import es.ggm.infor.softskills.model.NivelMuestraSoftSkill;
import es.ggm.infor.softskills.model.Profesor;
import es.ggm.infor.softskills.model.SoftSkill;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

        boolean pertenece = curso.getAlumnos().stream().anyMatch(a -> a.getId().equals(alumno.getId()));
        if (!pertenece) {
            log.warn("El alumno {} no pertenece al curso {}", alumno.getId(), curso.getId());
            throw new SecurityException("El alumno no pertenece al curso indicado");
        }

        Profesor profesor = Profesor.builder().id(request.getProfesorId()).build();
        NivelMuestraSoftSkill nivel = request.getNivel() != null
                ? request.getNivel()
                : NivelMuestraSoftSkill.NORMAL;

        MuestraSoftSkill muestra = MuestraSoftSkill.builder()
                .curso(curso)
                .alumno(alumno)
                .profesor(profesor)
                .softSkill(softSkill)
                .valor(request.getValor())
                .nivel(nivel)
                .pesoNivel(nivel.getPeso())
                .motivo(normalizarMotivo(request.getMotivo()))
                .fecha(LocalDateTime.now())
                .build();

        muestraRepository.save(muestra);
        softSkillTotalService.aplicarNuevaMuestra(muestra);
        log.info("Muestra registrada con exito: {}", muestra);
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
