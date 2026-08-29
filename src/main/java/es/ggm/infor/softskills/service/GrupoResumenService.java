package es.ggm.infor.softskills.service;

import es.ggm.infor.moodleintegration.client.IMoodleClient;
import es.ggm.infor.moodleintegration.dto.AlumnoMoodleDTO;
import es.ggm.infor.moodleintegration.exceptions.GeneralMoodleException;
import es.ggm.infor.softskills.dao.CursoRepository;
import es.ggm.infor.softskills.dao.GrupoRepository;
import es.ggm.infor.softskills.dao.SoftSkillRepository;
import es.ggm.infor.softskills.dao.TotalSoftSkillPorAlumnoGrupoRepository;
import es.ggm.infor.softskills.dto.AlumnoConTotalesDTO;
import es.ggm.infor.softskills.exception.GrupoMoodleAccessException;
import es.ggm.infor.softskills.model.Curso;
import es.ggm.infor.softskills.model.Grupo;
import es.ggm.infor.softskills.model.SoftSkill;
import es.ggm.infor.softskills.model.TotalSoftSkillPorAlumnoGrupo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GrupoResumenService {

    private static final Logger logger = LoggerFactory.getLogger(GrupoResumenService.class);

    private final GrupoRepository grupoRepository;
    private final TotalSoftSkillPorAlumnoGrupoRepository totalSoftSkillPorAlumnoGrupoRepository;
    private final CursoRepository cursoRepository;
    private final SoftSkillRepository softSkillRepository;
    private final IMoodleClient moodleClient;
    private final TotalesPorDefectoService totalesPorDefectoService;
    private final RankingCalculationService rankingCalculationService;

    public List<AlumnoConTotalesDTO> obtenerResumenGrupo(String token, String nivel, String cicloFormativo,
                                                         String grupo, String cursoEscolar) throws GeneralMoodleException {
        Grupo grupoAcademico = grupoRepository.findByNivelAndCicloFormativoAndGrupoAndCursoEscolar(
                nivel, cicloFormativo, grupo, cursoEscolar
        ).orElseThrow(() -> new IllegalArgumentException("Grupo no encontrado"));

        List<TotalSoftSkillPorAlumnoGrupo> totales = totalSoftSkillPorAlumnoGrupoRepository.findByGrupo(grupoAcademico);
        Map<Long, AlumnoConTotalesDTO> resumenMap = new LinkedHashMap<>();
        Map<Long, Map<Long, java.math.BigDecimal>> puntuacionesPorAlumno = new HashMap<>();
        Map<Long, Map<Long, Long>> muestrasPorAlumno = new HashMap<>();

        Map<Long, AlumnoMoodleDTO> alumnosMoodlePorId = obtenerDatosAlumnosGrupo(token, grupoAcademico);
        Collection<SoftSkill> softSkillsDelGrupo = obtenerSoftSkillsDelGrupo(grupoAcademico.getId());

        for (AlumnoMoodleDTO alumnoMoodle : alumnosMoodlePorId.values()) {
            AlumnoConTotalesDTO dto = new AlumnoConTotalesDTO();
            dto.setId(alumnoMoodle.id);
            dto.setNombre(alumnoMoodle.fullname);
            resumenMap.put(alumnoMoodle.id, dto);
        }

        for (TotalSoftSkillPorAlumnoGrupo total : totales) {
            Long alumnoId = total.getAlumno().getId();
            AlumnoConTotalesDTO dto = resumenMap.computeIfAbsent(alumnoId, id -> {
                AlumnoConTotalesDTO nuevo = new AlumnoConTotalesDTO();
                nuevo.setId(id);
                AlumnoMoodleDTO alumnoMoodle = alumnosMoodlePorId.get(id);
                if (alumnoMoodle != null) {
                    nuevo.setNombre(alumnoMoodle.fullname);
                }
                return nuevo;
            });

            puntuacionesPorAlumno
                    .computeIfAbsent(alumnoId, ignored -> new LinkedHashMap<>())
                    .put(total.getSoftSkill().getId(), total.getPuntuacionTotal());

            muestrasPorAlumno
                    .computeIfAbsent(alumnoId, ignored -> new LinkedHashMap<>())
                    .put(total.getSoftSkill().getId(), total.getNumMuestras() != null ? total.getNumMuestras() : 0L);
        }

        for (AlumnoConTotalesDTO dto : resumenMap.values()) {
            dto.setTotalesPorSkill(
                    totalesPorDefectoService.construirTotales(
                            softSkillsDelGrupo,
                            puntuacionesPorAlumno.get(dto.getId())
                    )
            );
            Map<Long, Long> muestrasPorSkill = muestrasPorAlumno.get(dto.getId());
            dto.setNumMuestrasTotales(rankingCalculationService.sumarMuestras(muestrasPorSkill));
            dto.setRankingScore(
                    rankingCalculationService.calcularRankingScore(
                            softSkillsDelGrupo,
                            dto.getTotalesPorSkill(),
                            muestrasPorSkill
                    )
            );
        }

        List<AlumnoConTotalesDTO> ranking = new ArrayList<>(resumenMap.values());
        rankingCalculationService.ordenarYAsignarPosiciones(ranking);

        return ranking;
    }

    private Map<Long, AlumnoMoodleDTO> obtenerDatosAlumnosGrupo(String token, Grupo grupoAcademico) throws GeneralMoodleException {
        Long cursoMoodleGrupoId = grupoAcademico.getCursoMoodleGrupoId();
        Long cursoConsultaId = cursoMoodleGrupoId;

        if (cursoConsultaId == null) {
            Optional<Curso> cursoReferencia = cursoRepository.findFirstByGrupoAcademico_Id(grupoAcademico.getId());
            if (cursoReferencia.isEmpty()) {
                return Map.of();
            }
            cursoConsultaId = cursoReferencia.get().getId();
            logger.warn(
                    "El grupo academico {} no tiene cursoMoodleGrupoId configurado. Se aplica fallback temporal usando el curso {}.",
                    grupoAcademico.getId(),
                    cursoConsultaId
            );
        }

        List<AlumnoMoodleDTO> alumnosMoodle;
        try {
            alumnosMoodle = moodleClient.getAlumnos(token, cursoConsultaId);
        } catch (GeneralMoodleException e) {
            logger.error(
                    "Moodle ha rechazado o fallado al consultar alumnos del grupo academico {} con curso Moodle {}.",
                    grupoAcademico.getId(),
                    cursoConsultaId,
                    e
            );
            throw new GrupoMoodleAccessException("No tienes permisos para consultar este grupo academico.", e);
        }

        Map<Long, AlumnoMoodleDTO> alumnosPorId = new LinkedHashMap<>();
        for (AlumnoMoodleDTO alumno : alumnosMoodle) {
            alumnosPorId.put(alumno.id, alumno);
        }
        return alumnosPorId;
    }

    private Collection<SoftSkill> obtenerSoftSkillsDelGrupo(Long grupoId) {
        return softSkillRepository.findByGrupoId(grupoId);
    }
}
