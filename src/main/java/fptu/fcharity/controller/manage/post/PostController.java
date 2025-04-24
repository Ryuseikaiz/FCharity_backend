package fptu.fcharity.controller.manage.post;

import fptu.fcharity.dto.post.PostRequestDTO;
import fptu.fcharity.dto.post.PostUpdateDto;
import fptu.fcharity.dto.post.PostReportRequest;
import fptu.fcharity.response.post.PostResponse;
import fptu.fcharity.service.manage.post.PostService;
import fptu.fcharity.service.manage.post.PostVoteService;
import fptu.fcharity.utils.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import fptu.fcharity.dto.post.PostReportRequest;

@RestController
@RequestMapping("/posts")
public class PostController {
    @Autowired // Thêm dòng này
    private JwtUtil jwtUtil;

    @Autowired
    private PostService postService;
    @Autowired
    private PostVoteService postVoteService;

    // Lấy tất cả Post
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        List<PostResponse> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }
    // Lấy Post theo ID
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable("id") UUID id) {
        PostResponse responseDTO = postService.getPostById(id);
        return ResponseEntity.ok(responseDTO);
    }

    // Tạo mới Post
    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequestDTO postRequestDTO) {
        PostResponse savedPostDTO = postService.createPost(postRequestDTO);
        return new ResponseEntity<>(savedPostDTO, HttpStatus.CREATED);
    }

    // Cập nhật Post theo ID
    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable("id") UUID id, @RequestBody PostUpdateDto postUpdateDTO) {
        try {
            PostResponse updatedPostDTO = postService.updatePost(id, postUpdateDTO);
            return ResponseEntity.ok(updatedPostDTO);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{postId}/vote")
    public ResponseEntity<Map<String, Object>> votePost(
            @PathVariable UUID postId,
            @RequestParam UUID userId,
            @RequestParam int vote
    ) {
        try {
            postVoteService.votePost(postId, userId, vote);
            int totalVotes = postVoteService.getTotalVotes(postId);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "postId", postId,
                    "totalVote", totalVotes
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage() != null ? e.getMessage() : "Lỗi không xác định"
            ));
        }
    }
    @GetMapping("/mine")
    public ResponseEntity<List<PostResponse>> getMyPosts(@RequestParam UUID userId) {
        System.out.println("Fetching posts for userId: " + userId);
        List<PostResponse> posts = postService.getPostsByUserId(userId);
        return ResponseEntity.ok(posts);
    }
    // PostController.java
    @DeleteMapping("/{id}")


    public ResponseEntity<Void> deletePost(
            @PathVariable("id") UUID id,
            @RequestHeader("Authorization") String token
    ) {
        try {
            UUID userId = jwtUtil.extractUserId(token.replace("Bearer ", ""));
            if (!postService.isPostOwner(id, userId)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            postService.deletePost(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{postId}/report")
    public ResponseEntity<?> reportPost(
            @PathVariable UUID postId,
            @RequestBody PostReportRequest request,
            @RequestHeader("Authorization") String token
    ) {
        try {
            // Lấy user ID từ token
            UUID reporterId = getUserIdFromToken(token.replace("Bearer ", ""));

            postService.reportPost(postId, reporterId, request.getReason());
            return ResponseEntity.ok().body(Map.of("success", true));

        } catch (ApiRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Hàm lấy user ID từ token (giả định)
    private UUID getUserIdFromToken(String token) {
        // Implement logic thực tế của bạn ở đây
        // Ví dụ: return jwtUtil.extractUserId(token);
        return UUID.randomUUID(); // Tạm thời dùng giá trị mẫu
    }

}