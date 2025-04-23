package fptu.fcharity.dto.organization;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ArticleLikeDTO {
    private UUID articleLikeId;
    private ArticleDTO article;
    private UserDTO user;
    private Instant createdAt;
}
