package fptu.fcharity.dto.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeminiPromptFeedback {
    private String blockReason;
    private String blockReasonMessage;
    private List<GeminiSafetyRating> safetyRatings;
}