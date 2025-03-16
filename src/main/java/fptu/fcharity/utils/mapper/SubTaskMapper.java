package fptu.fcharity.utils.mapper;

import fptu.fcharity.dto.project.SubTaskDto;
import fptu.fcharity.entity.SubTask;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
@Mapper(componentModel = "spring")
public interface SubTaskMapper  extends BaseMapper<SubTask, SubTaskDto> {
    SubTaskDto toDto(SubTask project);
    SubTask toEntity(SubTaskDto projectDto);
    void updateEntityFromDto(SubTaskDto dto, @MappingTarget SubTask entity);
}

