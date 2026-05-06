package es.ggm.infor.softskills.service;


import es.ggm.infor.moodleintegration.client.IMoodleClient;
import es.ggm.infor.moodleintegration.dto.AlumnoMoodleDTO;
import es.ggm.infor.moodleintegration.dto.CursoMoodleDTO;
import es.ggm.infor.moodleintegration.exceptions.GeneralMoodleException;
import es.ggm.infor.softskills.dao.CursoRepository;
import es.ggm.infor.softskills.dao.TotalSoftSkillPorAlumnoCursoRepository;
import es.ggm.infor.softskills.dto.mapper.AlumnoMapper;
import es.ggm.infor.softskills.dto.mapper.CursoMapper;
import es.ggm.infor.softskills.exception.CursoYaRegistradoException;
import es.ggm.infor.softskills.exception.GrupoNoResueltoException;
import es.ggm.infor.softskills.model.Alumno;
import es.ggm.infor.softskills.model.Curso;
import es.ggm.infor.softskills.model.Profesor;
import es.ggm.infor.softskills.model.SoftSkill;
import es.ggm.infor.softskills.model.TotalSoftSkillPorAlumnoCurso;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CursoService implements ICursoService {

    @Autowired
    private final CursoRepository cursoRepository;
    @Autowired
    private final CursoMapper cursoMapper;
    @Autowired
    private final IMoodleClient moodleClient;
    @Autowired
    private final IAlumnoService alumnoService;
    @Autowired
    private final ISoftSkillService softSkillService;
    @Autowired
    private final AlumnoMapper alumnoMapper;
    @Autowired
    private final TotalSoftSkillPorAlumnoCursoRepository totalSoftSkillPorAlumnoCursoRepository;
    @Autowired
    private final GrupoService grupoService;
    @Autowired
    private final TotalesPorDefectoService totalesPorDefectoService;

    private static final Logger logger = LoggerFactory.getLogger(CursoService.class);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void registrarCurso(String token, Long cursoId, Long idProfesor) throws GeneralMoodleException {

        if (cursoRepository.existsById(cursoId)) {
            throw new CursoYaRegistradoException("El curso con ID " + cursoId + " ya ha sido registrado previamente.");
        }

        Profesor profesor = Profesor.builder().id(idProfesor).build();
        Curso curso = Curso.builder()
                .id(cursoId)
                .profesor(profesor)
                .build();

        CursoMoodleDTO detallesCurso = moodleClient.getInfoCurso(token, cursoId);
        cursoMapper.updateFromDto(detallesCurso, curso);
        cursoMapper.aplicarIdNumberEnCurso(detallesCurso.idnumber, curso);
        validarCursoRegistrable(curso, cursoId, detallesCurso.idnumber);
        grupoService.resolverGrupoDesdeCurso(curso);

        List<AlumnoMoodleDTO> alumnosMoodle = moodleClient.getAlumnos(token, cursoId);
        alumnosMoodle = alumnosMoodle.stream()
                .filter(alumno -> !alumno.id.equals(idProfesor))
                .collect(Collectors.toList());

        List<Alumno> alumnos = alumnoService.insertarAlumnosSiNoExisten(alumnosMoodle);
        curso.setAlumnos(alumnos);

        List<SoftSkill> todasLasSoftSkills = softSkillService.getAllSoftSkills();
        curso.setSoftSkills(todasLasSoftSkills);

        cursoRepository.save(curso);
    }

    @Override
    public List<Curso> getCursosDelProfesor(String token, Long idProfesor) {
        List<CursoMoodleDTO> cursosMoodle;
        try {
            cursosMoodle = moodleClient.getCursos(token, idProfesor);
        } catch (GeneralMoodleException e) {
            logger.error("Error al obtener los cursos de Moodle para el profesor " + idProfesor, e);
            throw new RuntimeException(e);
        }

        List<Curso> cursosBD = cursoRepository.findByProfesor_Id(idProfesor);
        Map<Long, Curso> cursosBDMap = new HashMap<>();
        for (Curso curso : cursosBD) {
            curso.setRegistradoSk(true);
            cursosBDMap.put(curso.getId(), curso);
        }

        List<Curso> resultado = new ArrayList<>();

        for (CursoMoodleDTO dto : cursosMoodle) {
            Curso curso = cursosBDMap.get(dto.id);
            if (curso != null) {
                cursoMapper.updateFromDto(dto, curso);
            } else {
                curso = cursoMapper.fromDto(dto);
            }

            cursoMapper.aplicarIdNumberEnCurso(dto.idnumber, curso);
            grupoService.puedeRegistrarseEnSoftSkills(curso);
            grupoService.resolverGrupoDesdeCurso(curso);
            resultado.add(curso);
        }
        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public Curso obtenerCursoConAlumnos(String token, Long cursoId) throws GeneralMoodleException {
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado: " + cursoId));

        rellenarDetallesCurso(token, cursoId, curso);

        List<AlumnoMoodleDTO> datosMoodle = moodleClient.getAlumnos(token, cursoId);
        Map<Long, AlumnoMoodleDTO> dtoMap = datosMoodle.stream()
                .collect(Collectors.toMap(dto -> dto.id, dto -> dto));

        for (Alumno alumno : curso.getAlumnos()) {
            AlumnoMoodleDTO dto = dtoMap.get(alumno.getId());
            if (dto != null) {
                alumnoMapper.updateFromDto(dto, alumno);
            }
        }

        rellenarTotalesPorSkill(curso);
        curso.getAlumnos().sort(comparadorAlumnosPorApellido());
        return curso;
    }

    @Override
    public Curso getCursoById(Long cursoId) {
        return cursoRepository.findById(cursoId)
                .orElseThrow(() -> new IllegalArgumentException("Curso no encontrado: " + cursoId));
    }

    private void rellenarDetallesCurso(String token, Long cursoId, Curso curso) throws GeneralMoodleException {
        CursoMoodleDTO detallesCursoMoodle = moodleClient.getInfoCurso(token, cursoId);
        cursoMapper.updateFromDto(detallesCursoMoodle, curso);
        cursoMapper.aplicarIdNumberEnCurso(detallesCursoMoodle.idnumber, curso);
        grupoService.puedeRegistrarseEnSoftSkills(curso);
        grupoService.resolverGrupoDesdeCurso(curso);
    }

    private void validarCursoRegistrable(Curso curso, Long cursoId, String idNumberRecibido) {
        if (grupoService.puedeRegistrarseEnSoftSkills(curso)) {
            return;
        }

        String mensaje = "No se ha podido enlazar el curso con ningun grupo. idNumber recibido: "
                + (idNumberRecibido == null ? "<vacio>" : idNumberRecibido)
                + ". Formato esperado: nivel_ciclo_grupo_cursoEscolar o nivel_ciclo_grupo_cursoEscolar_sufijo.";
        logger.error("Error al registrar el curso {}: {}", cursoId, mensaje);
        throw new GrupoNoResueltoException(mensaje);
    }

    private void rellenarTotalesPorSkill(Curso curso) {
        List<Alumno> alumnos = curso.getAlumnos();
        if (alumnos == null || alumnos.isEmpty()) {
            return;
        }

        List<Long> alumnoIds = alumnos.stream()
                .map(Alumno::getId)
                .toList();

        List<TotalSoftSkillPorAlumnoCurso> totales = totalSoftSkillPorAlumnoCursoRepository
                .findByCursoIdAndAlumnoIdIn(curso.getId(), alumnoIds);

        Map<Long, Map<Long, BigDecimal>> totalesPorAlumno = new HashMap<>();
        for (TotalSoftSkillPorAlumnoCurso total : totales) {
            totalesPorAlumno
                    .computeIfAbsent(total.getAlumno().getId(), ignored -> new LinkedHashMap<>())
                    .put(total.getSoftSkill().getId(), total.getPuntuacionTotal());
        }

        for (Alumno alumno : alumnos) {
            alumno.setTotalesPorSkill(
                    totalesPorDefectoService.construirTotales(
                            curso.getSoftSkills(),
                            totalesPorAlumno.getOrDefault(alumno.getId(), Collections.emptyMap())
                    )
            );
        }
    }

    private Comparator<Alumno> comparadorAlumnosPorApellido() {
        Collator collator = Collator.getInstance(new Locale("es", "ES"));
        collator.setStrength(Collator.PRIMARY);

        Comparator<String> comparadorTexto = Comparator.nullsLast(collator::compare);
        return Comparator
                .comparing((Alumno alumno) -> normalizarOrdenacion(alumno.getApellidos()), comparadorTexto)
                .thenComparing(alumno -> normalizarOrdenacion(alumno.getNombre()), comparadorTexto)
                .thenComparing(alumno -> normalizarOrdenacion(alumno.getNombreCompleto()), comparadorTexto);
    }

    private String normalizarOrdenacion(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}
