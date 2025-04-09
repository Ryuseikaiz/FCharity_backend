package fptu.fcharity.service.manage.project;

import fptu.fcharity.dto.project.TimelineDto;
import fptu.fcharity.entity.Project;
import fptu.fcharity.entity.TaskPlan;
import fptu.fcharity.entity.Timeline;
import fptu.fcharity.repository.manage.project.ProjectRepository;
import fptu.fcharity.repository.manage.project.TaskPlanRepository;
import fptu.fcharity.repository.manage.project.TimelineRepository;
import fptu.fcharity.response.project.TaskPlanResponse;
import fptu.fcharity.utils.exception.ApiRequestException;
import fptu.fcharity.utils.mapper.TimelineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
@Service
public class TimelineService {
    @Autowired
    private TimelineRepository timelineRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private TimelineMapper timelineMapper;
    @Autowired
    private TaskPlanRepository taskPlanRepository;
    public void takeObject(Timeline t, TimelineDto tDto){
        if (tDto.getProjectId() != null) {
            Project project = projectRepository.findWithEssentialById(tDto.getProjectId());
            t.setProject(project);
        }
    }
    public Timeline addPhase(TimelineDto timelineDto) {
        List<Timeline> ongoingTimeline = timelineRepository.findOngoingPhaseByProjectId(timelineDto.getProjectId());
        if(!ongoingTimeline.isEmpty()){
            throw new ApiRequestException("Cannot create new phase while there is an ongoing phase");
        }
        Timeline t = timelineMapper.toEntity(timelineDto);
        t.setStartTime(Instant.now());
        takeObject(t, timelineDto);
        return timelineRepository.save(t);
    }
    public Timeline updatePhase(TimelineDto timelineDto) {
        Timeline t = timelineRepository.findWithEssentialById(timelineDto.getId());
        timelineMapper.updateEntityFromDto(timelineDto,t);
        takeObject(t, timelineDto);
        return timelineRepository.save(t);
    }
    public boolean deletePhase(UUID id) {
        Timeline t = timelineRepository.findWithEssentialById(id);
        if(!getTasksOfPhase(t.getId()).isEmpty()){
            throw new ApiRequestException("Cannot delete phase with tasks");
        }
        timelineRepository.delete(t);
        return true;
    }
    //get phase by id
    public Timeline getPhaseById(UUID phaseId) {
        Timeline t = timelineRepository.findWithEssentialById(phaseId);
        return t;
    }
    public List<TaskPlanResponse> getTasksOfPhase(UUID phaseId) {
        List<TaskPlan> ts = taskPlanRepository.findByPhaseId(phaseId);
        return ts.stream().map(TaskPlanResponse::new).toList();
    }

    public List<Timeline> getPhaseByProjectId(UUID projectId) {
        List<Timeline> ts = timelineRepository.findByProjectId(projectId);
        return ts;
    }

    public List<TaskPlanResponse> getSubtasksOfTask(UUID taskId) {
        List<TaskPlan> ts = taskPlanRepository.findByParentTaskId(taskId);
        return ts.stream().map(TaskPlanResponse::new).toList();
    }
}
