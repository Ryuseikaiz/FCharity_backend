package fptu.fcharity.controller.admindashboard;

import fptu.fcharity.dto.admindashboard.PostDTO;
import fptu.fcharity.service.admindashboard.ManagePostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/posts")
@RequiredArgsConstructor
public class ManagePostController {
    private final ManagePostService postService;

    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable UUID postId) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable UUID postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok("Post deleted successfully.");
    }

    @PutMapping("/approve/{postId}")
    public ResponseEntity<String> approvePost(@PathVariable UUID postId) {
        postService.approvePost(postId);
        return ResponseEntity.ok("Post has been approved successfully.");
    }

    @PutMapping("/hide/{postId}")
    public ResponseEntity<String> hidePost(@PathVariable UUID postId) {
        postService.hidePost(postId);
        return ResponseEntity.ok("Post has been hidden successfully.");
    }

}
