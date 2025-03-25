package fptu.fcharity.utils.mapper;

import fptu.fcharity.dto.project.TaskPlanDto;
import fptu.fcharity.entity.TaskPlan;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
@Mapper(componentModel = "spring")
public interface TaskPlanMapper extends BaseMapper<TaskPlan, TaskPlanDto> {
    TaskPlanDto toDto(TaskPlan project);
    TaskPlan toEntity(TaskPlanDto projectDto);
    void updateEntityFromDto(TaskPlanDto dto, @MappingTarget TaskPlan entity);
}

