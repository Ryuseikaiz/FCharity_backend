package fptu.fcharity.utils.mapper;

import fptu.fcharity.entity.Project;
import fptu.fcharity.dto.project.ProjectDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProjectMapper extends BaseMapper<Project,ProjectDto> {
    ProjectDto toDto(Project project);
    Project toEntity(ProjectDto projectDto);
    void updateEntityFromDto(ProjectDto dto, @MappingTarget Project entity);
}

