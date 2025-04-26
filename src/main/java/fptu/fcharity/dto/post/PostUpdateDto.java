package fptu.fcharity.dto.post;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
@Getter
@Setter
public class PostUpdateDto {
    private String title;
    private String content;
    private int vote;
    private List<UUID> tagIds;
    private List<String> imageUrls;
    private List<String> videoUrls;
    private String status; // Thêm trường status

}