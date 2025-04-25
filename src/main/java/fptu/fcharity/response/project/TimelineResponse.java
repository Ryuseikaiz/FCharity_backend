package fptu.fcharity.response.project;

import fptu.fcharity.entity.Timeline;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;
@Getter
@Setter
public class TimelineResponse {
    private UUID id;
    private UUID projectId;
    private String title;
    private Instant startTime;
    private Instant endTime;
    private String content;
    private String status;

    public TimelineResponse(Timeline t){
        this.id = t.getId();
        this.projectId = t.getProject().getId();
        this.title = t.getTitle();
        this.startTime = t.getStartTime();
        this.endTime = t.getEndTime();
        this.content = t.getContent();
        this.status = t.getStatus();
    }
}
