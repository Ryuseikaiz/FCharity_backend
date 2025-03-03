package fptu.fcharity.postdto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class PostResponseDTO {
    private UUID postId;
    private String title;
    private String content;
    private int vote;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID userId;
    private List<UUID> tagIds;     // Danh sách ID của Tag liên kết với Post
}
