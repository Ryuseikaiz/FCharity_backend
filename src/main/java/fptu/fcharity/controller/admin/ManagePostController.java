package fptu.fcharity.controller.admin;

import fptu.fcharity.dto.admindashboard.PostDTO;
import fptu.fcharity.dto.admindashboard.ReasonDTO;
import fptu.fcharity.service.admin.ManagePostService;
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
    public ResponseEntity<PostDTO> getPostById(@PathVariable UUID postId) throws Throwable {
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable UUID postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok("Post deleted successfully.");
    }

    @PutMapping("/unban/{postId}")
    public ResponseEntity<String> unbanPost(@PathVariable UUID postId) {
        postService.unbanPost(postId);
        return ResponseEntity.ok("Post has been unbanned successfully.");
    }

    @PutMapping("/ban/{postId}")
    public ResponseEntity<String> banPost(@PathVariable UUID postId) {
        postService.banPost(postId);
        return ResponseEntity.ok("Post has been banned successfully.");
    }

    @PutMapping("/activate/{postId}")
    public ResponseEntity<String> activatePost(@PathVariable UUID postId) {
        postService.activatePost(postId);
        return ResponseEntity.ok("Post has been activated successfully.");
    }

    @PutMapping("/reject/{postId}")
    public ResponseEntity<String> rejectPost(@PathVariable UUID postId, @RequestBody ReasonDTO reasonDTO) {
        postService.rejectPost(postId, reasonDTO);
        return ResponseEntity.ok("Post has been rejected successfully.");
    }
}
