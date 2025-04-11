package fptu.fcharity.response.post;

import fptu.fcharity.entity.Post;
import fptu.fcharity.entity.Taggable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostResponse {
    private Post post;
    private List<Taggable> taggables;
    private List<String> attachments;
}
