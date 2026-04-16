package es.ggm.infor.softskills.service;

import es.ggm.infor.moodleintegration.client.IMoodleClient;
import es.ggm.infor.moodleintegration.dto.AlumnoMoodleDTO;
import es.ggm.infor.moodleintegration.dto.UsuarioMoodleDTO;
import es.ggm.infor.moodleintegration.exceptions.GeneralMoodleException;
import es.ggm.infor.softskills.dao.AlumnoRepository;
import es.ggm.infor.softskills.dao.CursoRepository;
import es.ggm.infor.softskills.dao.TotalSoftSkillRepository;
import es.ggm.infor.softskills.dto.AlumnoConTotalesDTO;
import es.ggm.infor.softskills.model.Alumno;
import es.ggm.infor.softskills.model.Curso;
import es.ggm.infor.softskills.model.SoftSkill;
import es.ggm.infor.softskills.model.TotalSoftSkillPorAlumno;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
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

        List<TotalSoftSkillPorAlumno> totales = totalRepository.findByAlumnoId(idAlumno);
        List<SoftSkill> softSkills = softSkillService.getAllSoftSkills();

        AlumnoConTotalesDTO dto = new AlumnoConTotalesDTO();
        dto.setId(alumno.getId());
        dto.setNombre(resolverNombreAlumno(alumno, idAlumno, usuarioAutenticado, token, isTeacher));

        Map<Long, BigDecimal> totalesPorSkill = new LinkedHashMap<>();
        Map<Long, Long> muestrasPorSkill = new LinkedHashMap<>();
        for (TotalSoftSkillPorAlumno total : totales) {
            totalesPorSkill.put(total.getSoftSkill().getId(), total.getPuntuacionTotal());
            muestrasPorSkill.put(total.getSoftSkill().getId(), total.getNumMuestras() != null ? total.getNumMuestras() : 0L);
        }

        dto.setTotalesPorSkill(totalesPorDefectoService.construirTotales(softSkills, totalesPorSkill));
        dto.setNumMuestrasTotales(rankingCalculationService.sumarMuestras(muestrasPorSkill));
        dto.setRankingScore(rankingCalculationService.calcularRankingScore(softSkills, dto.getTotalesPorSkill(), muestrasPorSkill));
        dto.setPosicionRanking(calcularPosicionRanking(idAlumno, softSkills));

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

    private Integer calcularPosicionRanking(Long idAlumno, List<SoftSkill> softSkills) {
        List<Alumno> alumnos = alumnoRepository.findAll();
        List<TotalSoftSkillPorAlumno> totales = totalRepository.findAll();

        Map<Long, Map<Long, BigDecimal>> puntuacionesPorAlumno = new HashMap<>();
        Map<Long, Map<Long, Long>> muestrasPorAlumno = new HashMap<>();

        for (TotalSoftSkillPorAlumno total : totales) {
            Long alumnoId = total.getAlumno().getId();
            puntuacionesPorAlumno
                    .computeIfAbsent(alumnoId, ignored -> new LinkedHashMap<>())
                    .put(total.getSoftSkill().getId(), total.getPuntuacionTotal());
            muestrasPorAlumno
                    .computeIfAbsent(alumnoId, ignored -> new LinkedHashMap<>())
                    .put(total.getSoftSkill().getId(), total.getNumMuestras() != null ? total.getNumMuestras() : 0L);
        }

        List<AlumnoConTotalesDTO> ranking = new ArrayList<>();
        for (Alumno alumno : alumnos) {
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

        ranking.sort(
                Comparator.comparing(AlumnoConTotalesDTO::getRankingScore, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(AlumnoConTotalesDTO::getId, Comparator.nullsLast(Comparator.naturalOrder()))
        );

        for (int i = 0; i < ranking.size(); i++) {
            if (Objects.equals(ranking.get(i).getId(), idAlumno)) {
                return i + 1;
            }
        }

        throw new EntityNotFoundException("No hay resumen disponible para el alumno");
    }
}
