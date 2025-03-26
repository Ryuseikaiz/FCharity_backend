package fptu.fcharity.service.manage.project;

import fptu.fcharity.dto.project.TimelineDto;
import fptu.fcharity.entity.Project;
import fptu.fcharity.entity.Timeline;
import fptu.fcharity.repository.manage.project.ProjectRepository;
import fptu.fcharity.repository.manage.project.TimelineRepository;
import fptu.fcharity.utils.exception.ApiRequestException;
import fptu.fcharity.utils.mapper.TimelineMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public class TimelineService {
    @Autowired
    private TimelineRepository timelineRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private TimelineMapper timelineMapper;
    public void takeObject(Timeline t, TimelineDto tDto){
        if (tDto.getProjectId() != null) {
            Project project = projectRepository.findById(tDto.getProjectId())
                    .orElseThrow(() -> new ApiRequestException("Không tìm thấy Project"));
            t.setProject(project);
        }
    }
    public Timeline addPhase(TimelineDto timelineDto) {
        Timeline timeline = timelineMapper.toEntity(timelineDto);
        takeObject(timeline, timelineDto);
        timelineRepository.save(timeline);
        return timelineRepository.save(timeline);
    }
    public Timeline updatePhase(TimelineDto timelineDto) {
        Timeline t = timelineRepository.findById(timelineDto.getId()).orElseThrow(null);
        timelineMapper.updateEntityFromDto(timelineDto,t);
        takeObject(t, timelineDto);
        timelineRepository.save(t);
        return timelineRepository.save(t);
    }
    public Timeline deletePhase(UUID id) {
        Timeline t = timelineRepository.findById(id).orElseThrow(null);
        timelineRepository.delete(t);
        return timelineRepository.save(t);
    }
}
