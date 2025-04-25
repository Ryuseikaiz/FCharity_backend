package fptu.fcharity.service.manage.project;

import fptu.fcharity.dto.project.TaskPlanDto;
import fptu.fcharity.entity.TaskPlan;
import fptu.fcharity.entity.TaskPlanStatus;
import fptu.fcharity.entity.Timeline;
import fptu.fcharity.entity.User;
import fptu.fcharity.repository.manage.project.TaskPlanRepository;
import fptu.fcharity.repository.manage.project.TaskPlanStatusRepository;
import fptu.fcharity.repository.manage.project.TimelineRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.response.project.TaskPlanResponse;
import fptu.fcharity.service.manage.user.UserService;
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
    private UserService userService;
    @Autowired
    private TimelineRepository timelineRepository;
    @Autowired
    private TaskPlanStatusRepository taskPlanStatusRepository;
    @Autowired
    private UserRepository userRepository;
    /*
 * private UUID id;
 *     private UUID phaseId;
 *     private UUID userId;
 *     private String taskName;
 *     private String taskPlanDescription;
 *     private Instant startTime;
 *     private Instant endTime;
 *     private String taskPlanStatus;
 *     private Instant createdAt;
 *     private Instant updatedAt;
 *     private UUID parentTaskId;
 *
 * */


    public void takeObject(TaskPlan t, TaskPlanDto tDto){
        if (tDto.getPhaseId() != null) {
            Timeline timeline = timelineRepository.findWithEssentialById(tDto.getPhaseId());
            t.setPhase(timeline);
        }
        if (tDto.getUserId() != null) {
            User u = userRepository.findWithEssentialById(tDto.getUserId());
            t.setUser(u);
        }else{
            t.setUser(null);
        }
        if(tDto.getParentTaskId() != null){
            TaskPlan parent = taskPlanRepository.findWithEssentialById(tDto.getParentTaskId());
            t.setParentTask(parent);
        }
        if(tDto.getTaskPlanStatusId() != null){
            TaskPlanStatus status = taskPlanStatusRepository.findWithEssentialById(tDto.getTaskPlanStatusId());
            t.setStatus(status);
        }
    }
    public TaskPlanResponse addTask(TaskPlanDto tDto){
        TaskPlan  t = taskPlanMapper.toEntity(tDto);
        takeObject(t, tDto);
        t.setCreatedAt(Instant.now());
        return new TaskPlanResponse(taskPlanRepository.save(t));
    }
    public TaskPlanResponse updateTask(TaskPlanDto tDto){
        TaskPlan t = taskPlanRepository.findWithEssentialById(tDto.getId());
        if (tDto.getTaskName() != null) t.setTaskName(tDto.getTaskName());
        if (tDto.getTaskPlanDescription() != null) t.setTaskPlanDescription(tDto.getTaskPlanDescription());
        if (tDto.getStartTime() != null) t.setStartTime(tDto.getStartTime());
        if (tDto.getEndTime() != null) t.setEndTime(tDto.getEndTime());
        if (tDto.getCreatedAt() != null) t.setCreatedAt(tDto.getCreatedAt());
        takeObject(t, tDto);
        t.setUpdatedAt(Instant.now());
        return new TaskPlanResponse(taskPlanRepository.save(t));
    }
    public TaskPlanResponse cancelTask(UUID task_id){
        TaskPlan t = taskPlanRepository.findById(task_id).orElseThrow(null);
        List<TaskPlan> l = taskPlanRepository.findTaskPlanByParentTaskId(t.getId());
        taskPlanRepository.deleteAll(l);
        taskPlanRepository.delete(t);
        return new TaskPlanResponse(t);
    }

}
