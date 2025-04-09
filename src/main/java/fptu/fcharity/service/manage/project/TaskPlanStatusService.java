package fptu.fcharity.service.manage.project;

import fptu.fcharity.dto.project.TaskPlanStatusDto;
import fptu.fcharity.entity.TaskPlan;
import fptu.fcharity.entity.TaskPlanStatus;
import fptu.fcharity.repository.manage.project.TaskPlanRepository;
import fptu.fcharity.repository.manage.project.TaskPlanStatusRepository;
import fptu.fcharity.utils.exception.ApiRequestException;
import fptu.fcharity.utils.mapper.TaskPlanStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TaskPlanStatusService {
    @Autowired
    private TaskPlanStatusRepository taskPlanStatusRepository;
    @Autowired
    private TaskPlanRepository taskPlanRepository;
    @Autowired
    private TaskPlanStatusMapper taskPlanStatusMapper;
    public TaskPlanStatus addTaskStatus(TaskPlanStatusDto tDto){
        return taskPlanStatusRepository.save(taskPlanStatusMapper.toEntity(tDto));
    }
    public TaskPlanStatus updateTaskStatus(TaskPlanStatusDto tDto){
        return taskPlanStatusRepository.save(taskPlanStatusMapper.toEntity(tDto));
    }
    public boolean deleteTaskStatus(UUID status_id){
       TaskPlanStatus t =  taskPlanStatusRepository.findById(status_id).orElseThrow(null);
      List<TaskPlan> l =  taskPlanRepository.findTaskPlanByStatus(t);
      if(!l.isEmpty()){
          throw new ApiRequestException("Cannot delete status that is being used");
      }
      taskPlanStatusRepository.delete(t);
      return true;
    }
    public List<TaskPlanStatus> getAllStatus(){
        return taskPlanStatusRepository.findAll();
    }
}
