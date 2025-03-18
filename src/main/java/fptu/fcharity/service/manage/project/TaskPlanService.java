package fptu.fcharity.service.manage.project;

import fptu.fcharity.dto.project.TaskPlanDto;
import fptu.fcharity.entity.*;
import fptu.fcharity.repository.manage.project.ProjectRepository;
import fptu.fcharity.repository.manage.project.TaskPlanRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.response.project.TaskPlanFinalResponse;
import fptu.fcharity.response.project.TaskPlanResponse;
import fptu.fcharity.utils.exception.ApiRequestException;
import fptu.fcharity.utils.mapper.TaskPlanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TaskPlanService {
    @Autowired
    private TaskPlanRepository taskPlanRepository;
    @Autowired
    private TaskPlanMapper taskPlanMapper;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubTaskService subTaskService;

    public TaskPlanFinalResponse getTaskPlanById(UUID taskPlanId) {
        TaskPlan t =  taskPlanRepository.findWithProjectById(taskPlanId);
        List<SubTask> subTasks = subTaskService.getSubTaskOfTaskPlan(taskPlanId);
        return new TaskPlanFinalResponse(t,subTasks);
    }
    public List<TaskPlanResponse> getTaskPlanOfProject(UUID projectId) {
        List<TaskPlan> tp =  taskPlanRepository.findByProjectId(projectId);
        return tp.stream().map(TaskPlanResponse::new).toList();
    }
    public void takeObject(TaskPlan taskPlan, TaskPlanDto taskPlanDto){
        if (taskPlanDto.getUserId() != null) {
            User user = userRepository.findById(taskPlanDto.getUserId() )
                    .orElseThrow(() -> new ApiRequestException("Không tìm thấy User"));
            taskPlan.setUser(user);
        }
        if (taskPlanDto.getProjectId() != null) {
            Project project = projectRepository.findById(taskPlanDto.getProjectId())
                    .orElseThrow(() -> new ApiRequestException("Không tìm thấy Project"));
            taskPlan.setProject(project);
        }
    }
    public TaskPlanResponse addTaskPlan(TaskPlanDto taskPlanDto) {
        taskPlanDto.setCreatedAt(Instant.now());
        TaskPlan p =  taskPlanMapper.toEntity(taskPlanDto);
        takeObject(p,taskPlanDto);
        return new TaskPlanResponse(taskPlanRepository.save(p));
    }
    public TaskPlan updateTaskPlan(TaskPlanDto taskPlanDto) {
        taskPlanDto.setUpdatedAt(Instant.now());
        TaskPlan p = taskPlanRepository.findWithProjectById(taskPlanDto.getId());
        taskPlanMapper.updateEntityFromDto(taskPlanDto, p);
        takeObject(p,taskPlanDto);
        return taskPlanRepository.save(p);
    }
    public void deleteTaskPlan(UUID taskPlanId) {
        TaskPlan taskPlan = taskPlanRepository.findById(taskPlanId).orElseThrow(() -> new ApiRequestException("Không tìm thấy TaskPlan"));
        taskPlanRepository.delete(taskPlan);
    }
}
