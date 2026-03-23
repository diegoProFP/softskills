package es.ggm.infor.softskills.dto.mapper;
import es.ggm.infor.moodleintegration.dto.CursoMoodleDTO;
import es.ggm.infor.softskills.model.Curso;
import org.mapstruct.*;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CursoMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "shortname", target = "nombreCorto")
    @Mapping(source = "fullname", target = "nombreLargo")
    @Mapping(source = "displayname", target = "nombreVisible")
    Curso fromDto(CursoMoodleDTO dto);

    @Mapping(source = "shortname", target = "nombreCorto")
    @Mapping(source = "fullname", target = "nombreLargo")
    @Mapping(source = "displayname", target = "nombreVisible")
    void updateFromDto(CursoMoodleDTO dto, @MappingTarget Curso curso);

    @AfterMapping
    default void mapIdNumber(CursoMoodleDTO dto, @MappingTarget Curso curso) {
        aplicarIdNumberEnCurso(dto.idnumber, curso);
    }

    default void aplicarIdNumberEnCurso(String idNumber, Curso curso) {
        curso.setIdNumber(idNumber);
        curso.setNivel(null);
        curso.setCicloFormativo(null);
        curso.setGrupo(null);
        curso.setCursoEscolar(null);

        if (idNumber == null || idNumber.isBlank()) {
            return;
        }

        String[] partes = idNumber.split("_");
        if (partes.length <= 4) {
            return;
        }

        curso.setNivel(partes[0]);
        curso.setCicloFormativo(partes[1]);
        curso.setGrupo(partes[2]);
        curso.setCursoEscolar(normalizarCursoEscolarId(partes[3]));
    }

    default String normalizarCursoEscolarId(String cursoEscolarRaw) {
        if (cursoEscolarRaw == null || cursoEscolarRaw.isBlank()) {
            return cursoEscolarRaw;
        }

        if (cursoEscolarRaw.length() == 4) {
            return cursoEscolarRaw.substring(0, 2) + "-" + cursoEscolarRaw.substring(2);
        }

        return cursoEscolarRaw;
    }
}

