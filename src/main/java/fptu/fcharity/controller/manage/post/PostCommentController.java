package fptu.fcharity.controller.manage.post;

import fptu.fcharity.dto.post.CommentDTO;
import fptu.fcharity.service.manage.post.commentService;
import fptu.fcharity.service.authentication.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/comments")
public class PostCommentController {

    private static final Logger logger = LoggerFactory.getLogger(PostCommentController.class);

    @Autowired
    private CommentService commentService;

    @Autowired
    private JwtService jwtService;

    private Integer validateTokenAndGetUserId(String authHeader) {
        String token = authHeader.substring(7).trim();
        String username = jwtService.extractUsername(token);
        if (username == null) {
            throw new IllegalArgumentException("Invalid token");
        }
        // Convert username to user ID as needed (depends on your implementation)
        return Integer.parseInt(username); // Adjust this if necessary
    }

    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CommentDTO commentDTO,
                                           @RequestHeader("Authorization") String authHeader) {
        try {
            if (commentDTO == null || commentDTO.getPostId() == null) {
                logger.error("Invalid comment or post data received.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid comment or post data.");
            }

            Integer userId = validateTokenAndGetUserId(authHeader);
            List<CommentDTO> updatedComments = commentService.createComment(commentDTO, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedComments);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error creating comment: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while creating the comment.");
        }
    }

    @PutMapping("/{commentId}/soft-delete")
    public ResponseEntity<?> softDeleteComment(
            @PathVariable("commentId") Integer commentId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Integer userId = validateTokenAndGetUserId(authHeader);
            List<CommentDTO> updatedComments = commentService.deleteComment(commentId, userId);
            return ResponseEntity.ok().body(updatedComments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the comment.");
        }
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<?> getCommentDetail(@PathVariable("commentId") Integer commentId) {
        try {
            CommentDTO commentDTO = commentService.getCommentById(commentId);
            if (commentDTO == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment not found.");
            }
            return ResponseEntity.ok(commentDTO);
        } catch (Exception e) {
            logger.error("Error retrieving comment detail: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving the comment detail.");
        }
    }

    @GetMapping("/post/{postId}/sorted")
    public ResponseEntity<Page<CommentDTO>> getSortedComments(@PathVariable("postId") Integer postId,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        try {
            Page<CommentDTO> comments = commentService.getCommentsByPostId(postId, page, size);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            logger.error("Error retrieving sorted comments: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTO> editComment(
            @PathVariable("commentId") Integer commentId,
            @RequestBody CommentDTO commentDTO,
            @RequestHeader("Authorization") String authHeader) {
        try {
            Integer userId = validateTokenAndGetUserId(authHeader);
            if (commentDTO == null || commentDTO.getContent() == null || commentDTO.getContent().isEmpty()) {
                throw new IllegalArgumentException("Comment content cannot be empty.");
            }
            CommentDTO updatedComment = commentService.editComment(commentId, commentDTO.getContent(), userId);
            return ResponseEntity.ok(updatedComment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Handled IllegalArgumentException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        logger.error("Handled RuntimeException: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error occurred.");
    }
}
