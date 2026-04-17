package es.ggm.infor.softskills.service;

import es.ggm.infor.moodleintegration.client.IMoodleClient;
import es.ggm.infor.moodleintegration.dto.AlumnoMoodleDTO;
import es.ggm.infor.moodleintegration.dto.UsuarioMoodleDTO;
import es.ggm.infor.moodleintegration.exceptions.GeneralMoodleException;
import es.ggm.infor.softskills.dao.AlumnoRepository;
import es.ggm.infor.softskills.dao.CursoRepository;
import es.ggm.infor.softskills.dao.SoftSkillRepository;
import es.ggm.infor.softskills.dao.TotalSoftSkillPorAlumnoGrupoRepository;
import es.ggm.infor.softskills.dao.TotalSoftSkillRepository;
import es.ggm.infor.softskills.dto.AlumnoConTotalesDTO;
import es.ggm.infor.softskills.model.Alumno;
import es.ggm.infor.softskills.model.Curso;
import es.ggm.infor.softskills.model.Grupo;
import es.ggm.infor.softskills.model.SoftSkill;
import es.ggm.infor.softskills.model.TotalSoftSkillPorAlumno;
import es.ggm.infor.softskills.model.TotalSoftSkillPorAlumnoGrupo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

// Servicio para obtener un alumno con sus totales
@Service
@RequiredArgsConstructor
public class AlumnoDetalleService {
    private final AlumnoRepository alumnoRepository;
    private final TotalSoftSkillRepository totalRepository;
    private final ISoftSkillService softSkillService;
    private final TotalesPorDefectoService totalesPorDefectoService;
    private final CursoRepository cursoRepository;
    private final SoftSkillRepository softSkillRepository;
    private final TotalSoftSkillPorAlumnoGrupoRepository totalSoftSkillPorAlumnoGrupoRepository;
    private final IMoodleClient moodleClient;
    private final RankingCalculationService rankingCalculationService;

    public AlumnoConTotalesDTO obtenerDetalleAlumno(Long idAlumno) {
        Alumno alumno = alumnoRepository.findById(idAlumno)
                .orElseThrow(() -> new EntityNotFoundException("Alumno no encontrado"));

        List<TotalSoftSkillPorAlumno> totales = totalRepository.findByAlumnoId(idAlumno);
        List<SoftSkill> softSkills = softSkillService.getAllSoftSkills();

        AlumnoConTotalesDTO dto = new AlumnoConTotalesDTO();
        dto.setId(alumno.getId());
        dto.setNombre(alumno.getNombre());

        Map<Long, java.math.BigDecimal> totalesPorSkill = new LinkedHashMap<>();
        for (TotalSoftSkillPorAlumno total : totales) {
            totalesPorSkill.put(total.getSoftSkill().getId(), total.getPuntuacionTotal());
        }
        dto.setTotalesPorSkill(totalesPorDefectoService.construirTotales(softSkills, totalesPorSkill));

        return dto;
    }

    public AlumnoConTotalesDTO obtenerResumenAlumno(Long idAlumno,
                                                    UsuarioMoodleDTO usuarioAutenticado,
                                                    String token,
                                                    boolean isTeacher,
                                                    boolean isStudent) throws GeneralMoodleException {
        Alumno alumno = alumnoRepository.findById(idAlumno)
                .orElseThrow(() -> new EntityNotFoundException("Alumno no encontrado"));

        validarAcceso(idAlumno, usuarioAutenticado, isTeacher, isStudent);

        Grupo grupoAcademico = resolverGrupoReferencia(idAlumno, usuarioAutenticado.getUserid(), isTeacher);
        List<TotalSoftSkillPorAlumnoGrupo> totalesGrupo = grupoAcademico != null
                ? totalSoftSkillPorAlumnoGrupoRepository.findByGrupo(grupoAcademico)
                : List.of();
        Collection<SoftSkill> softSkills = grupoAcademico != null
                ? softSkillRepository.findByGrupoId(grupoAcademico.getId())
                : softSkillService.getAllSoftSkills();

        AlumnoConTotalesDTO dto = new AlumnoConTotalesDTO();
        dto.setId(alumno.getId());
        dto.setNombre(resolverNombreAlumno(alumno, idAlumno, usuarioAutenticado, token, isTeacher));

        Map<Long, BigDecimal> totalesPorSkill = new LinkedHashMap<>();
        Map<Long, Long> muestrasPorSkill = new LinkedHashMap<>();
        for (TotalSoftSkillPorAlumnoGrupo total : filtrarTotalesAlumno(totalesGrupo, idAlumno)) {
            totalesPorSkill.put(total.getSoftSkill().getId(), total.getPuntuacionTotal());
            muestrasPorSkill.put(total.getSoftSkill().getId(), total.getNumMuestras() != null ? total.getNumMuestras() : 0L);
        }

        dto.setTotalesPorSkill(totalesPorDefectoService.construirTotales(softSkills, totalesPorSkill));
        dto.setNumMuestrasTotales(rankingCalculationService.sumarMuestras(muestrasPorSkill));
        dto.setRankingScore(rankingCalculationService.calcularRankingScore(softSkills, dto.getTotalesPorSkill(), muestrasPorSkill));
        dto.setPosicionRanking(grupoAcademico != null
                ? calcularPosicionRanking(idAlumno, grupoAcademico, softSkills, totalesGrupo)
                : null);

        return dto;
    }

    private void validarAcceso(Long idAlumno,
                               UsuarioMoodleDTO usuarioAutenticado,
                               boolean isTeacher,
                               boolean isStudent) {
        Long usuarioId = usuarioAutenticado.getUserid();
        boolean esSuPropioResumen = Objects.equals(usuarioId, idAlumno);

        if (isStudent && esSuPropioResumen) {
            return;
        }

        if (isTeacher && cursoRepository.existsByProfesor_IdAndAlumnos_Id(usuarioId, idAlumno)) {
            return;
        }

        throw new AccessDeniedException("No tienes permiso para consultar el resumen de este alumno");
    }

    private String resolverNombreAlumno(Alumno alumno,
                                        Long idAlumno,
                                        UsuarioMoodleDTO usuarioAutenticado,
                                        String token,
                                        boolean isTeacher) throws GeneralMoodleException {
        if (Objects.equals(usuarioAutenticado.getUserid(), idAlumno) && usuarioAutenticado.getFullname() != null) {
            return usuarioAutenticado.getFullname();
        }

        Optional<Curso> cursoConAlumno = isTeacher
                ? cursoRepository.findFirstByProfesor_IdAndAlumnos_Id(usuarioAutenticado.getUserid(), idAlumno)
                : cursoRepository.findFirstByAlumnos_Id(idAlumno);

        if (cursoConAlumno.isPresent()) {
            List<AlumnoMoodleDTO> alumnosMoodle = moodleClient.getAlumnos(token, cursoConAlumno.get().getId());
            for (AlumnoMoodleDTO alumnoMoodle : alumnosMoodle) {
                if (Objects.equals(alumnoMoodle.id, idAlumno)) {
                    return alumnoMoodle.fullname;
                }
            }
        }

        return alumno.getNombre();
    }

    private Grupo resolverGrupoReferencia(Long idAlumno, Long usuarioId, boolean isTeacher) {
        Optional<Curso> cursoReferencia = isTeacher
                ? cursoRepository.findFirstByProfesor_IdAndAlumnos_Id(usuarioId, idAlumno)
                : cursoRepository.findFirstByAlumnos_Id(idAlumno);
        return cursoReferencia
                .map(Curso::getGrupoAcademico)
                .orElse(null);
    }

    private List<TotalSoftSkillPorAlumnoGrupo> filtrarTotalesAlumno(List<TotalSoftSkillPorAlumnoGrupo> totales,
                                                                    Long idAlumno) {
        List<TotalSoftSkillPorAlumnoGrupo> resultado = new ArrayList<>();
        for (TotalSoftSkillPorAlumnoGrupo total : totales) {
            if (Objects.equals(total.getAlumno().getId(), idAlumno)) {
                resultado.add(total);
            }
        }
        return resultado;
    }

    private Integer calcularPosicionRanking(Long idAlumno,
                                            Grupo grupoAcademico,
                                            Collection<SoftSkill> softSkills,
                                            List<TotalSoftSkillPorAlumnoGrupo> totales) {
        Map<Long, Alumno> alumnosGrupo = obtenerAlumnosGrupo(grupoAcademico);
        Map<Long, Map<Long, BigDecimal>> puntuacionesPorAlumno = new HashMap<>();
        Map<Long, Map<Long, Long>> muestrasPorAlumno = new HashMap<>();

        for (TotalSoftSkillPorAlumnoGrupo total : totales) {
            Long alumnoId = total.getAlumno().getId();
            alumnosGrupo.putIfAbsent(alumnoId, total.getAlumno());
            puntuacionesPorAlumno
                    .computeIfAbsent(alumnoId, ignored -> new LinkedHashMap<>())
                    .put(total.getSoftSkill().getId(), total.getPuntuacionTotal());
            muestrasPorAlumno
                    .computeIfAbsent(alumnoId, ignored -> new LinkedHashMap<>())
                    .put(total.getSoftSkill().getId(), total.getNumMuestras() != null ? total.getNumMuestras() : 0L);
        }

        List<AlumnoConTotalesDTO> ranking = new ArrayList<>();
        for (Alumno alumno : alumnosGrupo.values()) {
            AlumnoConTotalesDTO dto = new AlumnoConTotalesDTO();
            dto.setId(alumno.getId());
            dto.setNombre(alumno.getNombre());
            dto.setTotalesPorSkill(
                    totalesPorDefectoService.construirTotales(
                            softSkills,
                            puntuacionesPorAlumno.get(alumno.getId())
                    )
            );
            dto.setRankingScore(
                    rankingCalculationService.calcularRankingScore(
                            softSkills,
                            dto.getTotalesPorSkill(),
                            muestrasPorAlumno.get(alumno.getId())
                    )
            );
            ranking.add(dto);
        }

        rankingCalculationService.ordenarYAsignarPosiciones(ranking);

        for (AlumnoConTotalesDTO dto : ranking) {
            if (Objects.equals(dto.getId(), idAlumno)) {
                return dto.getPosicionRanking();
            }
        }

        throw new EntityNotFoundException("No hay resumen disponible para el alumno");
    }

    private Map<Long, Alumno> obtenerAlumnosGrupo(Grupo grupoAcademico) {
        Map<Long, Alumno> alumnosGrupo = new LinkedHashMap<>();
        for (Curso curso : cursoRepository.findByGrupoAcademico_Id(grupoAcademico.getId())) {
            if (curso.getAlumnos() == null) {
                continue;
            }

            for (Alumno alumno : curso.getAlumnos()) {
                alumnosGrupo.putIfAbsent(alumno.getId(), alumno);
            }
        }
        return alumnosGrupo;
    }
}
