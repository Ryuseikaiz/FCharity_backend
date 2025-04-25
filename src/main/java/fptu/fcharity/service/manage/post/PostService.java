package fptu.fcharity.service.manage.post;

import fptu.fcharity.dto.post.PostUpdateDto;
import fptu.fcharity.entity.Post;
import fptu.fcharity.entity.PostVote;
import fptu.fcharity.entity.PostVoteId;
import fptu.fcharity.entity.User;
import fptu.fcharity.dto.post.PostRequestDTO;
import fptu.fcharity.repository.ObjectAttachmentRepository;
import fptu.fcharity.repository.TagRepository;
import fptu.fcharity.repository.manage.post.CommentRepository;
import fptu.fcharity.repository.manage.post.PostVoteRepository;
import fptu.fcharity.response.post.PostResponse;
import fptu.fcharity.repository.manage.post.PostRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.repository.TaggableRepository;
import fptu.fcharity.service.ObjectAttachmentService;
import fptu.fcharity.service.TaggableService;
import fptu.fcharity.utils.constants.PostStatus;
import fptu.fcharity.utils.constants.TaggableType;
import fptu.fcharity.utils.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import fptu.fcharity.entity.PostReport;
import fptu.fcharity.repository.manage.post.PostReportRepository;

@Service
public class PostService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaggableService taggableService;
    @Autowired
    private ObjectAttachmentService objectAttachmentService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    // Lấy tất cả các Post có trạng thái ACTIVE
    public List<PostResponse> getAllPosts() {
        List<Post> posts = postRepository.findAllWithInclude()
                .stream()
                .filter(post -> PostStatus.APPROVED.equals(post.getPostStatus()))
                .toList();
        return posts.stream().map(post -> new PostResponse(post,
                        taggableService.getTagsOfObject(post.getId(), TaggableType.POST),
                        objectAttachmentService.getAttachmentsOfObject(post.getId(), TaggableType.POST)))
                .toList();
    }


    // Lấy Post theo ID nếu có trạng thái ACTIVE
    public PostResponse getPostById(UUID postId) {
        Post post = postRepository.findWithIncludeById(postId);
        if (post == null) {
            throw new ApiRequestException("Post not found or not active");
        }
        return new PostResponse(post,
                taggableService.getTagsOfObject(post.getId(), TaggableType.POST),
                objectAttachmentService.getAttachmentsOfObject(post.getId(), TaggableType.POST));
    }

    public PostResponse createPost(PostRequestDTO postRequestDTO) {
        // Lấy đối tượng User từ DB theo userId từ DTO
        Optional<User> optionalUser = userRepository.findById(postRequestDTO.getUserId());
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found with id: " + postRequestDTO.getUserId());
        }
        User user = optionalUser.get();

        // Tạo post với trạng thái PENDING
        Post post = new Post(user, postRequestDTO.getTitle(), postRequestDTO.getContent());
        post.setPostStatus(PostStatus.PENDING); // Đảm bảo trạng thái là PENDING

        Post savedPost = postRepository.save(post);

        // Lưu tag và đính kèm hình ảnh/video
        taggableService.addTaggables(savedPost.getId(), postRequestDTO.getTagIds(), TaggableType.POST);
        if( postRequestDTO.getImageUrls() != null){
            objectAttachmentService.saveAttachments(savedPost.getId(), postRequestDTO.getImageUrls(), TaggableType.POST);
        }
        if( postRequestDTO.getVideoUrls() != null){
            objectAttachmentService.saveAttachments(savedPost.getId(), postRequestDTO.getVideoUrls(), TaggableType.POST);
        }
        simpMessagingTemplate.convertAndSend("/topic/post-notifications", "User " + user.getEmail() + " has created a new post.");

        return new PostResponse(savedPost,
                taggableService.getTagsOfObject(savedPost.getId(), TaggableType.POST),
                objectAttachmentService.getAttachmentsOfObject(savedPost.getId(), TaggableType.POST));
    }


    public PostResponse updatePost(UUID postId, PostUpdateDto postUpdateDTO) {
        Post post = postRepository.findWithIncludeById(postId);
        post.setTitle(postUpdateDTO.getTitle());
        post.setContent(postUpdateDTO.getContent());
        post.setVote(postUpdateDTO.getVote());
        post.setUpdatedAt(Instant.now());
        Post updatedPost = postRepository.save(post);
        if (postUpdateDTO.getTagIds() != null) {
            taggableService.updateTaggables(updatedPost.getId(), postUpdateDTO.getTagIds(),TaggableType.POST);
        } else {
            taggableService.updateTaggables(updatedPost.getId(), new ArrayList<>(),TaggableType.POST);
        }
        objectAttachmentService.clearAttachments(updatedPost.getId(), TaggableType.POST);
        objectAttachmentService.saveAttachments(updatedPost.getId(), postUpdateDTO.getImageUrls(), TaggableType.POST);
        objectAttachmentService.saveAttachments(updatedPost.getId(), postUpdateDTO.getVideoUrls(), TaggableType.POST);
        return new PostResponse(updatedPost,
                taggableService.getTagsOfObject(updatedPost.getId(), TaggableType.POST),
                objectAttachmentService.getAttachmentsOfObject(updatedPost.getId(),TaggableType.POST));
    }


    @Transactional
    public void deletePost(UUID postId) {
        // Xóa các comment và attachment liên quan
        CommentRepository.deleteByPostId(postId);
        ObjectAttachmentRepository.deleteByPostId(postId);
        postRepository.deleteById(postId);
    }

    public List<PostResponse> getPostsByUserId(UUID userId) {
        List<Post> posts = postRepository.findByUserId(userId);
        return posts.stream()
                .map(PostResponse::fromEntity)
                .collect(Collectors.toList());
    }
    @Autowired
    private PostReportRepository postReportRepository;

    // Thêm phương thức reportPost
    @Transactional
    public void reportPost(UUID postId, UUID reporterId, String reason) {
        // Validate post
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiRequestException("Không tìm thấy bài viết"));

        // Validate reporter
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new ApiRequestException("Người dùng không tồn tại"));

        // Tạo báo cáo
        PostReport report = new PostReport();
        report.setPost(post);
        report.setReporter(reporter);
        report.setReason(reason);
        report.setReportDate(Instant.now());

        postReportRepository.save(report);
    }
    // Trong PostService
    public boolean isPostOwner(UUID postId, UUID userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiRequestException("Không tìm thấy bài viết"));
        return post.getUser().getId().equals(userId);
    }
}