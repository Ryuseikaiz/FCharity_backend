package fptu.fcharity.dto.project;

import fptu.fcharity.entity.Project;
import fptu.fcharity.entity.Timeline;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.util.UUID;
@Getter
@Setter
public class TimelineDto {
    private UUID id;
    private UUID projectId;
    private String title;
    private Instant startTime;
    private Instant endTime;
    private String content;
}
