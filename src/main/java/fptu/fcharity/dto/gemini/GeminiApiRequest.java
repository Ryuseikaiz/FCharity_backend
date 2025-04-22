package fptu.fcharity.dto.gemini;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeminiApiRequest {
    private List<GeminiRequestContent> contents;
    private List<GeminiSafetySetting> safetySettings;
    private GeminiGenerationConfig generationConfig;
}