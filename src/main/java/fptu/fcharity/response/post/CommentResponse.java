package fptu.fcharity.response.post;

import fptu.fcharity.entity.User;
import fptu.fcharity.response.authentication.UserResponse;
import lombok.*;
import java.time.LocalDateTime;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private UUID commentId;
    private UUID postId;
    private UserResponse user;
    private String userName;
    private String userAvatar;
    private String content;
    private int vote;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
