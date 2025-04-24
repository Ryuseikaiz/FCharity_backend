package fptu.fcharity.response.post;

import fptu.fcharity.entity.Comment;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter

public class CommentFinalResponse {
    private CommentResponse comment;
    private List<CommentResponse> replies;
    public CommentFinalResponse() {}
    public CommentFinalResponse(CommentResponse comment, List<CommentResponse> replies) {
        this.comment = comment;
        this.replies = replies;
    }
}