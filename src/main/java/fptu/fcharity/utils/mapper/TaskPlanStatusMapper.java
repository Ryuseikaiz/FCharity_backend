package fptu.fcharity.utils.mapper;

import fptu.fcharity.dto.project.TaskPlanStatusDto;
import fptu.fcharity.entity.TaskPlanStatus;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaskPlanStatusMapper extends BaseMapper<TaskPlanStatus, TaskPlanStatusDto> {
    TaskPlanStatusDto toDto(TaskPlanStatus t);
    TaskPlanStatus toEntity(TaskPlanStatusDto tDto);
    void updateEntityFromDto(TaskPlanStatusDto dto, @MappingTarget TaskPlanStatus entity);
}
