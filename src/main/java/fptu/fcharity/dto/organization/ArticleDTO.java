package fptu.fcharity.dto.organization;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ArticleDTO {
    private UUID articleId;
    private OrganizationDTO organization;
    private String title;
    private String content;
    private UserDTO author;
    private Instant createdAt;
    private Instant updatedAt;
    private int views;
    private int likes;
}
