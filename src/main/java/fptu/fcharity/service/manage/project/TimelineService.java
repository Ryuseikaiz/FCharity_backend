package fptu.fcharity.service.manage.project;

import fptu.fcharity.dto.project.FinalTimelineDto;
import fptu.fcharity.dto.project.TimelineDto;
import fptu.fcharity.entity.Project;
import fptu.fcharity.entity.TaskPlan;
import fptu.fcharity.entity.Timeline;
import fptu.fcharity.repository.manage.project.ProjectRepository;
import fptu.fcharity.repository.manage.project.TaskPlanRepository;
import fptu.fcharity.repository.manage.project.TimelineRepository;
import fptu.fcharity.response.project.TaskPlanResponse;
import fptu.fcharity.response.project.TimelineFinalResponse;
import fptu.fcharity.response.project.TimelineResponse;
import fptu.fcharity.service.ObjectAttachmentService;
import fptu.fcharity.utils.constants.TaggableType;
import fptu.fcharity.utils.constants.project.ProjectStatus;
import fptu.fcharity.utils.exception.ApiRequestException;
import fptu.fcharity.utils.mapper.TimelineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
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
    @Autowired
    private ObjectAttachmentService objectAttachmentService;
    public void takeObject(Timeline t, TimelineDto tDto){
        if (tDto.getProjectId() != null) {
            Project project = projectRepository.findWithEssentialById(tDto.getProjectId());
            t.setProject(project);
        }
    }
    public TimelineResponse addPhase(TimelineDto timelineDto) {
        List<Timeline> ongoingTimeline = timelineRepository.findOngoingPhaseByProjectId(timelineDto.getProjectId());
        if(!ongoingTimeline.isEmpty()){
            throw new ApiRequestException("Cannot create new phase while there is an ongoing phase");
        }

        Timeline t = timelineMapper.toEntity(timelineDto);
        t.setStatus(ProjectStatus.ACTIVE);
        t.setStartTime(Instant.now());
        takeObject(t, timelineDto);
        return new TimelineResponse(timelineRepository.save(t));
    }
    public TimelineFinalResponse updatePhase(FinalTimelineDto dto) {
        Timeline t = timelineRepository.findWithEssentialById(dto.getPhase().getId());
        if(dto.getPhase().getTitle()!=null){
            t.setTitle(dto.getPhase().getTitle());
        }
        if(dto.getPhase().getContent()!=null){
            t.setContent(dto.getPhase().getContent());
        }
        takeObject(t, dto.getPhase());
        return getTimelineFinalResponse(dto, t);
    }

    private TimelineFinalResponse getTimelineFinalResponse(FinalTimelineDto dto, Timeline t) {
        objectAttachmentService.clearAttachments(dto.getPhase().getId(), TaggableType.PHASE);
        if(dto.getImageUrls() != null){
            objectAttachmentService.saveAttachments(dto.getPhase().getId(), dto.getImageUrls(), TaggableType.PHASE);
        }
        if(dto.getVideoUrls() != null){
            objectAttachmentService.saveAttachments(dto.getPhase().getId(), dto.getVideoUrls(), TaggableType.PHASE);
        }
        TimelineResponse res = new TimelineResponse(timelineRepository.save(t));
        List<String> attachments = objectAttachmentService.getAttachmentsOfObject(t.getId(), TaggableType.PHASE);
        return new TimelineFinalResponse(res,attachments);
    }

    public TimelineResponse deletePhase(UUID id) {
        Timeline t = timelineRepository.findWithEssentialById(id);
        if(!getTasksOfPhase(t.getId()).isEmpty()){
            throw new ApiRequestException("Cannot delete phase with tasks");
        }
        timelineRepository.delete(t);
        return new TimelineResponse(t);
    }
    //get phase by id
    public TimelineFinalResponse getPhaseById(UUID phaseId) {
        Timeline t = timelineRepository.findWithEssentialById(phaseId);
        TimelineResponse tResponse = new TimelineResponse(t);
        return new TimelineFinalResponse(tResponse,objectAttachmentService.getAttachmentsOfObject(t.getId(), TaggableType.PHASE));
    }
    public List<TaskPlanResponse> getTasksOfPhase(UUID phaseId) {
        List<TaskPlan> ts = taskPlanRepository.findByPhaseId(phaseId);
        return ts.stream().map(TaskPlanResponse::new).toList();
    }

    public List<TimelineFinalResponse> getPhaseByProjectId(UUID projectId) {
        List<Timeline> ts = timelineRepository.findByProjectId(projectId);
        return ts.stream()
                .sorted(Comparator.comparing(Timeline::getStartTime).reversed())
                .map(a->new TimelineFinalResponse(new TimelineResponse(a),objectAttachmentService.getAttachmentsOfObject(a.getId(), TaggableType.PHASE)))
                .toList();
    }

    public List<TaskPlanResponse> getSubtasksOfTask(UUID taskId) {
        List<TaskPlan> ts = taskPlanRepository.findByParentTaskId(taskId);
        return ts.stream().map(TaskPlanResponse::new).toList();
    }

    public TaskPlanResponse getTaskById(UUID taskId) {
        TaskPlan t = taskPlanRepository.findWithEssentialById(taskId);
        return new TaskPlanResponse(t);
    }

    public List<TaskPlanResponse> getTaskOfProject(UUID projectId) {
        List<TaskPlan> ts = taskPlanRepository.findByProjectId(projectId);
        return ts.stream().map(TaskPlanResponse::new).toList();
    }
    public TimelineFinalResponse endPhase(FinalTimelineDto dto) {
        Timeline t = timelineRepository.findWithEssentialById(dto.getPhase().getId());
        takeObject(t, dto.getPhase());
        t.setStatus(ProjectStatus.FINISHED);
        t.setEndTime(Instant.now());
        return getTimelineFinalResponse(dto, t);
    }
}
