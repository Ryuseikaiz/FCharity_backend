package fptu.fcharity.dto.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeminiResponsePart {
    private String text;
}