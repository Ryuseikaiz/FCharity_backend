package fptu.fcharity.service.manage.project;

import fptu.fcharity.dto.project.TaskPlanStatusDto;
import fptu.fcharity.entity.TaskPlan;
import fptu.fcharity.entity.TaskPlanStatus;
import fptu.fcharity.entity.Timeline;
import fptu.fcharity.repository.manage.project.TaskPlanRepository;
import fptu.fcharity.repository.manage.project.TaskPlanStatusRepository;
import fptu.fcharity.repository.manage.project.TimelineRepository;
import fptu.fcharity.response.project.TaskPlanStatusResponse;
import fptu.fcharity.utils.exception.ApiRequestException;
import fptu.fcharity.utils.mapper.TaskPlanStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class TaskPlanStatusService {
    @Autowired
    private TaskPlanStatusRepository taskPlanStatusRepository;
    @Autowired
    private TaskPlanRepository taskPlanRepository;
    @Autowired
    private TaskPlanStatusMapper taskPlanStatusMapper;
    @Autowired
    private TimelineRepository timelineRepository;

    public TaskPlanStatusResponse addTaskStatus(TaskPlanStatusDto tDto){
        Timeline timeline = timelineRepository.findWithEssentialById(tDto.getPhaseId());
        TaskPlanStatus t = taskPlanStatusMapper.toEntity(tDto);
        t.setPhase(timeline);
        TaskPlanStatus res = taskPlanStatusRepository.save(t);
        return new TaskPlanStatusResponse(res);
    }
    public void addDefaultTaskStatus(UUID phaseId){
        Timeline timeline = timelineRepository.findWithEssentialById(phaseId);
        TaskPlanStatus t = new TaskPlanStatus();
        t.setPhase(timeline);
        t.setStatusName("To Do".toUpperCase(Locale.ROOT));
        taskPlanStatusRepository.save(t);
        t = new TaskPlanStatus();
        t.setPhase(timeline);
        t.setStatusName("In Progress".toUpperCase(Locale.ROOT));
        taskPlanStatusRepository.save(t);
        t = new TaskPlanStatus();
        t.setPhase(timeline);
        t.setStatusName("Done".toUpperCase(Locale.ROOT));
        taskPlanStatusRepository.save(t);
    }
    public TaskPlanStatusResponse updateTaskStatus(TaskPlanStatusDto tDto){
        TaskPlanStatus t = taskPlanStatusMapper.toEntity(tDto);
        TaskPlanStatus res = taskPlanStatusRepository.save(t);
        return new TaskPlanStatusResponse(res);
    }
    public TaskPlanStatusResponse deleteTaskStatus(UUID status_id){
       TaskPlanStatus t =  taskPlanStatusRepository.findById(status_id).orElseThrow(null);
      List<TaskPlan> l =  taskPlanRepository.findTaskPlanByStatus(t);
      if(!l.isEmpty()){
          throw new ApiRequestException("Cannot delete status that is being used");
      }
      taskPlanStatusRepository.delete(t);
      return new TaskPlanStatusResponse(t);
    }
    public List<TaskPlanStatusResponse> getAllStatusByProject(UUID projectId){
        List<TaskPlanStatus> taskPlanStatuses = taskPlanStatusRepository.findAllByProjectId(projectId);
        return taskPlanStatuses.stream().map(TaskPlanStatusResponse::new).toList();
    }
}
