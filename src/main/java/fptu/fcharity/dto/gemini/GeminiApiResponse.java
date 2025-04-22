package fptu.fcharity.dto.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeminiApiResponse {
    private List<GeminiCandidate> candidates;
    private GeminiPromptFeedback promptFeedback;
}