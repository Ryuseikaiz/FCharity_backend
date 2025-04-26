package fptu.fcharity.response.project;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
@NoArgsConstructor
public class TimelineFinalResponse {
    private TimelineResponse phase;
    private List<String> attachments;
    public TimelineFinalResponse(TimelineResponse timeline, List<String> attachments) {
        this.phase = timeline;
        this.attachments = attachments;
    }
}
