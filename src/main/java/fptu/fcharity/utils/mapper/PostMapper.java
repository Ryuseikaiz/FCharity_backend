package fptu.fcharity.utils.mapper;

import fptu.fcharity.entity.Post;
import fptu.fcharity.entity.Taggable;
import fptu.fcharity.response.post.PostResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public class PostMapper {
    // Phương thức chuyển đổi Post sang PostResponseDTO
    public PostResponse convertToDTO(Post post, List<Taggable> taggableList) {
        PostResponse dto = new PostResponse();
        dto.setPost(post);
        dto.setTaggables(taggableList);
        return dto;
    }
}
