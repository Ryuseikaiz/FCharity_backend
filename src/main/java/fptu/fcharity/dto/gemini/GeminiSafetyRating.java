package fptu.fcharity.dto.gemini;

import lombok.Data;

@Data
public class GeminiSafetyRating {
    private String category;
    private String probability;
    private Boolean blocked;
}