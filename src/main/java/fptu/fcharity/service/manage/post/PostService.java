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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import fptu.fcharity.entity.PostReport;
import fptu.fcharity.repository.manage.post.PostReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(PostService.class);

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

        return new PostResponse(savedPost,
                taggableService.getTagsOfObject(savedPost.getId(), TaggableType.POST),
                objectAttachmentService.getAttachmentsOfObject(savedPost.getId(), TaggableType.POST));
    }


    // Thêm method hidePost
    @Transactional
    public void hidePost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiRequestException("Post not found"));

        post.setPostStatus(PostStatus.HIDDEN);
        postRepository.save(post);
    }

    public PostResponse updatePost(UUID postId, PostUpdateDto postUpdateDTO) {
        try {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new ApiRequestException("Post not found with id: " + postId));
            post.setPostStatus(PostStatus.PENDING);
            post.setTitle(postUpdateDTO.getTitle());
            post.setContent(postUpdateDTO.getContent());
            post.setUpdatedAt(Instant.now());

            processTagsAndAttachments(post, postUpdateDTO);

            Post updatedPost = postRepository.save(post);
            return buildPostResponse(updatedPost);
        } catch (Exception e) {
            log.error("Update post error: {}", e.getMessage());
            throw new ApiRequestException("Update failed: " + e.getMessage());
        }
    }

    private void processTagsAndAttachments(Post post, PostUpdateDto dto) {
        // Xử lý tags
        if (dto.getTagIds() != null) {
            taggableService.updateTaggables(post.getId(), dto.getTagIds(), TaggableType.POST);
        }

        // Xử lý attachments
        objectAttachmentService.clearAttachments(post.getId(), TaggableType.POST);
        if (dto.getImageUrls() != null) {
            objectAttachmentService.saveAttachments(post.getId(), dto.getImageUrls(), TaggableType.POST);
        }
        if (dto.getVideoUrls() != null) {
            objectAttachmentService.saveAttachments(post.getId(), dto.getVideoUrls(), TaggableType.POST);
        }
    }

    private PostResponse buildPostResponse(Post post) {
        return new PostResponse(
                post,
                taggableService.getTagsOfObject(post.getId(), TaggableType.POST),
                objectAttachmentService.getAttachmentsOfObject(post.getId(), TaggableType.POST)
        );
    }


    @Transactional
    public void deletePost(UUID postId) {
        Post post = postRepository.findWithIncludeById(postId);
        // Xóa các comment và attachment liên quan
        objectAttachmentService.clearAttachments(postId, TaggableType.POST);
        postRepository.delete(post);
        ObjectAttachmentRepository.deleteByPostId(postId);
        postRepository.deleteById(postId);
    }

    public List<PostResponse> getPostsByUserId(UUID userId) {
        List<Post> posts = postRepository.findByUserId(userId);
        return posts.stream()
                .map(post -> new PostResponse(
                        post,
                        taggableService.getTagsOfObject(post.getId(), TaggableType.POST),
                        objectAttachmentService.getAttachmentsOfObject(post.getId(), TaggableType.POST)
                ))
                .collect(Collectors.toList());
    }
    @Autowired
    private PostReportRepository postReportRepository;

    @Transactional
    public void reportPost(UUID postId, UUID reporterId, String reason) {
        // Validate post
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiRequestException("Không tìm thấy bài viết"));

        // Validate reporter
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new ApiRequestException("Người dùng không tồn tại"));

        // Kiểm tra user đã report chưa
        boolean alreadyReported = postReportRepository.existsByPostAndReporter(post, reporter);
        if (alreadyReported) {
            throw new ApiRequestException("Bạn đã báo cáo bài viết này trước đó");
        }

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
    public List<PostResponse> getTopVotedPosts(int limit) {
        List<Post> topPosts = postRepository.findAll().stream()
                .filter(p -> PostStatus.APPROVED.equals(p.getPostStatus()))
                .sorted(Comparator.comparing(Post::getVote, Comparator.nullsFirst(Comparator.reverseOrder())))
                .limit(limit)
                .toList();

        return topPosts.stream()
                .map(post -> new PostResponse(
                        post,
                        taggableService.getTagsOfObject(post.getId(), TaggableType.POST),
                        objectAttachmentService.getAttachmentsOfObject(post.getId(), TaggableType.POST)
                ))
                .toList();
    }

}