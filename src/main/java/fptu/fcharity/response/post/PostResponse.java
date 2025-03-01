package fptu.fcharity.response.post;

import fptu.fcharity.entity.Taggable;
import fptu.fcharity.entity.Post;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostResponse {
    private Post post;
    private List<Taggable> taggables;

}
