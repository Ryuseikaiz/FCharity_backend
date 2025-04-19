package fptu.fcharity.dto.post;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {
    private UUID commentId;
    private UUID postId;
    private UUID userId;
    private String content;
    private int vote;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID parentCommentId;
    private List<CommentDTO> replies;
}
