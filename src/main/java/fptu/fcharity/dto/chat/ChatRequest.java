package fptu.fcharity.dto.chat;

import lombok.Data;
import java.util.List;

@Data
public class ChatRequest {
    private String message;
    private List<ChatMessage> history;
}