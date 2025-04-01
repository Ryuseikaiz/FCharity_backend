package fptu.fcharity.service.admin;

import fptu.fcharity.dto.admindashboard.PostDTO;
import fptu.fcharity.entity.Post;
import fptu.fcharity.repository.manage.post.PostRepository;
import fptu.fcharity.utils.constants.PostStatus;
import fptu.fcharity.utils.exception.ApiRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagePostService {
    private final PostRepository postRepository;

    public List<PostDTO> getAllPosts() {
        return postRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public PostDTO getPostById(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiRequestException("Post not found with ID: " + postId));
        return convertToDTO(post);
    }

    @Transactional
    public void deletePost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiRequestException("Post not found with ID: " + postId));
        postRepository.delete(post);
    }

    @Transactional
    public void unbanPost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiRequestException("Post not found with ID: " + postId));

        if (!PostStatus.BANNED.equals(post.getPostStatus())) {
            throw new ApiRequestException("Post is not in HIDDEN status");
        }

        post.setPostStatus(PostStatus.APPROVED);
        postRepository.save(post);
    }

    @Transactional
    public void banPost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiRequestException("Post not found with ID: " + postId));

        if (!PostStatus.APPROVED.equals(post.getPostStatus())) {
            throw new ApiRequestException("Only active posts can be hidden.");
        }

        post.setPostStatus(PostStatus.BANNED);
        postRepository.save(post);
    }

    @Transactional
    public void activatePost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiRequestException("Post not found with ID: " + postId));

        if (!PostStatus.PENDING.equals(post.getPostStatus())) {
            throw new ApiRequestException("Only pending posts can be activated.");
        }

        post.setPostStatus(PostStatus.APPROVED);
        postRepository.save(post);
    }

    @Transactional
    public void rejectPost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiRequestException("Post not found with ID: " + postId));

        if (!PostStatus.PENDING.equals(post.getPostStatus())) {
            throw new ApiRequestException("Only pending posts can be rejected.");
        }

        post.setPostStatus(PostStatus.REJECTED);
        postRepository.save(post);
    }

    private PostDTO convertToDTO(Post post) {
        return new PostDTO(
                post.getId(),
                post.getUser() != null ? post.getUser().getId() : null,
                post.getTitle(),
                post.getContent(),
                post.getVote(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getPostStatus(),
                post.getReason()
        );
    }
}
