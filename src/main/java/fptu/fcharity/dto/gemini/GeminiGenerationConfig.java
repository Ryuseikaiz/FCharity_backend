package fptu.fcharity.dto.gemini;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeminiGenerationConfig {
    private Float temperature;
    private Integer topK;
    private Float topP;
    private Integer maxOutputTokens;
}