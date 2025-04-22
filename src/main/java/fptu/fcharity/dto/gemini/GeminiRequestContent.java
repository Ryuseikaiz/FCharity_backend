package fptu.fcharity.dto.gemini;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeminiRequestContent {
    private String role;
    private List<GeminiRequestPart> parts;

}