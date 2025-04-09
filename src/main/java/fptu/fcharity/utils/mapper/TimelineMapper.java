package fptu.fcharity.utils.mapper;

import fptu.fcharity.dto.project.ProjectDto;
import fptu.fcharity.dto.project.TimelineDto;
import fptu.fcharity.entity.Project;
import fptu.fcharity.entity.Timeline;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TimelineMapper extends BaseMapper<Timeline, TimelineDto> {
    TimelineDto toDto(Timeline t);
    Timeline toEntity(TimelineDto tDto);
    void updateEntityFromDto(TimelineDto dto, @MappingTarget Timeline entity);

}
