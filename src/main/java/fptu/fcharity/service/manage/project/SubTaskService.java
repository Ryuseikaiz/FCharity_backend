package fptu.fcharity.service.manage.project;


import fptu.fcharity.dto.project.SubTaskDto;
import fptu.fcharity.entity.SubTask;
import fptu.fcharity.entity.TaskPlan;
import fptu.fcharity.entity.User;
import fptu.fcharity.repository.manage.project.ProjectRepository;
import fptu.fcharity.repository.manage.project.SubTaskRepository;
import fptu.fcharity.repository.manage.project.TaskPlanRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.utils.exception.ApiRequestException;
import fptu.fcharity.utils.mapper.SubTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class SubTaskService {
    @Autowired
    private SubTaskRepository subTaskRepository;
    @Autowired
    private TaskPlanRepository taskPlanRepository;
    @Autowired
    private SubTaskMapper subTaskMapper;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;

    public SubTask getSubTaskById(UUID subTaskId) {
        return subTaskRepository.findWithIncludeById(subTaskId);
    }
    public List<SubTask> getSubTaskOfTaskPlan(UUID taskPlanId) {
        return subTaskRepository.findByTaskPlanId(taskPlanId);
    }
    public void takeObject(SubTask subTask, SubTaskDto subTaskDto){
        if (subTaskDto.getUserId() != null) {
            User user = userRepository.findById(subTaskDto.getUserId() )
                    .orElseThrow(() -> new ApiRequestException("Không tìm thấy User"));
            subTask.setUser(user);
        }
        if (subTaskDto.getTaskPlanId() != null) {
            TaskPlan t = taskPlanRepository.findById(subTaskDto.getTaskPlanId())
                    .orElseThrow(() -> new ApiRequestException("Không tìm thấy Project"));
            subTask.setTaskPlan(t);
        }
    }
    public SubTask addSubTask(SubTaskDto subTaskDto) {
        subTaskDto.setCreatedAt(Instant.now());
        SubTask p =  subTaskMapper.toEntity(subTaskDto);
        takeObject(p,subTaskDto);
        return subTaskRepository.save(p);
    }
    public SubTask updateSubTask(SubTaskDto subTaskDto) {
        subTaskDto.setUpdatedAt(Instant.now());
        SubTask p =  subTaskRepository.findWithIncludeById(subTaskDto.getId());
        subTaskMapper.updateEntityFromDto(subTaskDto, p);
        takeObject(p,subTaskDto);
        return subTaskRepository.save(p);
    }

    public void deleteSubTask(UUID id) {
        SubTask s = subTaskRepository.findById(id).orElseThrow(() -> new ApiRequestException("Không tìm thấy SubTask"));
        subTaskRepository.delete(s);
    }
}
