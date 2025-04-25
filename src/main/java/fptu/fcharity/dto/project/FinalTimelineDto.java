package fptu.fcharity.dto.project;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class FinalTimelineDto {
    private TimelineDto phase;
    private List<String> imageUrls;
    private List<String> videoUrls;
}
