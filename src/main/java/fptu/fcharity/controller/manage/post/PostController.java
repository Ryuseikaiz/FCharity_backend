package fptu.fcharity.controller.manage.post;

import fptu.fcharity.dto.post.PostRequestDTO;
import fptu.fcharity.dto.post.PostUpdateDto;
import fptu.fcharity.response.post.PostResponse;
import fptu.fcharity.service.manage.post.PostService;
import fptu.fcharity.service.manage.post.PostVoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
public class PostController {

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

    // Xóa Post theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") UUID id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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
    @GetMapping("/tag/{tagName}")
    public ResponseEntity<List<PostResponse>> getPostsByTag(@PathVariable String tagName) {
        List<PostResponse> posts = postService.getPostsByTag(tagName);
        return ResponseEntity.ok(posts);
    }



}
