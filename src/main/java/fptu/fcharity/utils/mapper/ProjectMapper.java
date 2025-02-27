package fptu.fcharity.utils.mapper;

import fptu.fcharity.dto.project.ProjectDto;
import fptu.fcharity.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProjectMapper extends BaseMapper<Project,ProjectDto> {
    @Mapping(source = "organization.id", target = "organizationId")
    @Mapping(source = "leader.id", target = "leaderId")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "tag.id", target = "tagId")
    @Mapping(source = "walletAddress.id", target = "walletId")
    ProjectDto toDto(Project project);

    @Mapping(target = "organization", ignore = true) // Avoid fetching full entity
    @Mapping(target = "leader", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "tag", ignore = true)
    @Mapping(target = "walletAddress", ignore = true)
    Project toEntity(ProjectDto projectDto);

    @Mapping(target = "organization", ignore = true)
    @Mapping(target = "leader", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "tag", ignore = true)
    @Mapping(target = "walletAddress", ignore = true)
    void updateEntityFromDto(ProjectDto dto, @MappingTarget Project entity);
}

