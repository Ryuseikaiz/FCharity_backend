package fptu.fcharity.dto.admindashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PostDTO {
    private UUID id;
    private UUID userId;
    private String title;
    private String content;
    private Integer vote;
    private Instant createdAt;
    private Instant updatedAt;
    private String postStatus;
}
