package fptu.fcharity.dto.chat;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatErrorResponse {
    private String error;
    private String details;

    public ChatErrorResponse(String error) {
        this.error = error;
    }
}