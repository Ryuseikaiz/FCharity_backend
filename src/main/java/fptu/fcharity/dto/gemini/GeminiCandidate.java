package fptu.fcharity.dto.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeminiCandidate {
    private GeminiResponseContent content;
    private String finishReason;
    private List<GeminiSafetyRating> safetyRatings;
    private Integer index;
    private Integer tokenCount;
}