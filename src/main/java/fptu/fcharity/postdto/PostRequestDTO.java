package fptu.fcharity.postdto;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class PostRequestDTO {
    private String title;
    private String content;
    private int vote;
    private UUID userId;  // ID của User liên kết với Post
}
