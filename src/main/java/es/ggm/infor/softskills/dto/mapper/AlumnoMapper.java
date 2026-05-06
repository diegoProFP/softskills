package es.ggm.infor.softskills.dto.mapper;

import es.ggm.infor.moodleintegration.dto.AlumnoMoodleDTO;
import es.ggm.infor.softskills.model.Alumno;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlumnoMapper {
    @Mapping(source = "firstname", target = "nombre")
    @Mapping(source = "lastname", target = "apellidos")
    @Mapping(source = "fullname", target = "nombreCompleto")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "email", target = "email")
    void updateFromDto(AlumnoMoodleDTO dto, @MappingTarget Alumno alumno);
}
