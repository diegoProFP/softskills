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
}

