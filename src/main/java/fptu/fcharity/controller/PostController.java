package fptu.fcharity.controller;

import fptu.fcharity.dto.post.PostRequestDTO;
import fptu.fcharity.dto.post.PostUpdateDto;
import fptu.fcharity.response.post.PostResponse;
import fptu.fcharity.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

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
        try {
            PostResponse savedPostDTO = postService.createPost(postRequestDTO);
            return new ResponseEntity<>(savedPostDTO, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
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
}
