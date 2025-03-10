package fptu.fcharity.dto.post;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class PostRequestDTO {
    private String title;
    private String content;
    private int vote;
    private UUID userId;
    private List<UUID> tagIds;
    private String status;
}
