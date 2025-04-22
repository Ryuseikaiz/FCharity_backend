package fptu.fcharity.dto.gemini;

import lombok.Data;
import java.util.List;

@Data
public class GeminiResponseContent {
    private List<GeminiResponsePart> parts;
    private String role;
}