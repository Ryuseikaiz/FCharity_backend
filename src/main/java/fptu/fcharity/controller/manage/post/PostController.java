package fptu.fcharity.controller.manage.post;

import fptu.fcharity.dto.post.PostRequestDTO;
import fptu.fcharity.dto.post.PostUpdateDto;
import fptu.fcharity.dto.post.PostReportRequest;
import fptu.fcharity.entity.Post;
import fptu.fcharity.entity.User;
import fptu.fcharity.repository.manage.post.PostRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.response.post.PostResponse;
import fptu.fcharity.service.ObjectAttachmentService;
import fptu.fcharity.service.TaggableService;
import fptu.fcharity.service.manage.post.PostService;
import fptu.fcharity.service.manage.post.PostVoteService;
import fptu.fcharity.utils.constants.PostStatus;
import fptu.fcharity.utils.constants.TaggableType;
import fptu.fcharity.utils.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import fptu.fcharity.dto.post.PostReportRequest;
import fptu.fcharity.controller.manage.post.JwtUtil;
@RestController
@RequestMapping("/posts")
public class PostController {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TaggableService taggableService;

    @Autowired
    private ObjectAttachmentService objectAttachmentService;

    @Autowired
    private UserRepository userRepository;

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
    // PostController.java
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable UUID id, @RequestBody PostUpdateDto postUpdateDTO) {
        try {
            PostResponse response = postService.updatePost(id, postUpdateDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", response
            ));
        } catch (ApiRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()
                    ));
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
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable("id") UUID id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{postId}/report")
    public ResponseEntity<?> reportPost(
            @PathVariable UUID postId,
            @RequestBody PostReportRequest request // Nhận reporterId từ body
    ) {
        try {
            postService.reportPost(postId, request.getReporterId(), request.getReason());
            return ResponseEntity.ok().body(Map.of("success", true));
        } catch (ApiRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/top-voted")
    public ResponseEntity<List<PostResponse>> getTopVotedPosts(@RequestParam(defaultValue = "2") int limit) {
        List<PostResponse> topPosts = postService.getTopVotedPosts(limit);
        return ResponseEntity.ok(topPosts);
    }
    @PostMapping("/{postId}/hide")
    public ResponseEntity<?> hidePost(
            @PathVariable UUID postId,
            @RequestParam UUID userId
    ) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ApiRequestException("User not found"));

            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new ApiRequestException("Post not found"));

            if (!post.getUser().getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            post.setPostStatus(PostStatus.HIDDEN);
            Post updatedPost = postRepository.save(post);

            return ResponseEntity.ok(new PostResponse(
                    updatedPost,
                    taggableService.getTagsOfObject(postId, TaggableType.POST),
                    objectAttachmentService.getAttachmentsOfObject(postId, TaggableType.POST)
            ));
        } catch (ApiRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @PostMapping("/{postId}/unhide")
    public ResponseEntity<?> unhidePost(
            @PathVariable UUID postId,
            @RequestParam UUID userId
    ) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ApiRequestException("User not found"));

            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new ApiRequestException("Post not found"));

            if (!post.getUser().getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            post.setPostStatus(PostStatus.APPROVED); // Hoặc PENDING tùy logic nghiệp vụ
            Post updatedPost = postRepository.save(post);

            return ResponseEntity.ok(new PostResponse(
                    updatedPost,
                    taggableService.getTagsOfObject(postId, TaggableType.POST),
                    objectAttachmentService.getAttachmentsOfObject(postId, TaggableType.POST)
            ));
        } catch (ApiRequestException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}