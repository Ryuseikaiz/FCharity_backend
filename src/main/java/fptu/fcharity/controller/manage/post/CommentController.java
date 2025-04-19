package fptu.fcharity.controller.manage.post;

import fptu.fcharity.dto.post.CommentDTO;
import fptu.fcharity.entity.Comment;
import fptu.fcharity.response.post.CommentFinalResponse;
import fptu.fcharity.response.post.CommentResponse;
import fptu.fcharity.service.manage.post.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@RequestBody CommentDTO commentDTO) {
        CommentResponse r = commentService.createComment(commentDTO);
        return ResponseEntity.ok(r);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentFinalResponse>> getCommentsByPost(
            @PathVariable UUID postId,
            @RequestParam(defaultValue = "0") int page, // Mặc định page = 1
            @RequestParam(defaultValue = "5") int size) {
        List<CommentFinalResponse> l = commentService.getCommentsByPost(postId, page, size);
        return ResponseEntity.ok(l);
    }


    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable UUID commentId, @RequestBody CommentDTO commentDTO) {
        return ResponseEntity.ok(commentService.updateComment(commentId, commentDTO));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
    // CommentController.java
    @PostMapping("/{commentId}/reply")
    public ResponseEntity<CommentResponse> createReply(
            @PathVariable UUID commentId,
            @RequestBody CommentDTO commentDTO) {
        return ResponseEntity.ok(commentService.createReply(commentId, commentDTO));
    }

    @PostMapping("/{commentId}/vote")
    public ResponseEntity<Map<String, Object>> voteComment(
            @PathVariable UUID commentId,
            @RequestParam UUID userId,
            @RequestParam int vote) {
        System.out.println("Vote request - commentId: " + commentId + ", userId: " + userId + ", vote: " + vote);
        try {
            commentService.voteComment(commentId, userId, vote);
            Comment comment = commentService.getCommentById(commentId);
            int totalVotes = comment.getVote();
            System.out.println("Vote successful - totalVotes: " + totalVotes);
            return ResponseEntity.ok()
                    .body(Map.of(
                            "success", true,
                            "commentId", commentId,
                            "totalVote", totalVotes
                    ));
        } catch (Exception e) {
            System.out.println("Vote error: " + e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage() != null ? e.getMessage() : "Lỗi không xác định"));

        }
    }
    @GetMapping("/post/{postId}/all")
    public ResponseEntity<List<CommentResponse>> getAllCommentsByPost(@PathVariable UUID postId ) {
            List<CommentResponse> l = commentService.getAllCommentsByPostId(postId);
            return ResponseEntity.ok(l);
    }
}
