package fptu.fcharity.service;

import fptu.fcharity.dto.admindashboard.PostDTO;
import fptu.fcharity.entity.Post;
import fptu.fcharity.repository.ManagePostRepository;
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
    private final ManagePostRepository postRepository;

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
    public void approvePost(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiRequestException("Post not found with ID: " + postId));

        if (!PostStatus.HIDDEN.equals(post.getPostStatus())) {
            throw new ApiRequestException("Post is not in HIDDEN status");
        }

        post.setPostStatus(PostStatus.ACTIVE);
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
                post.getPostStatus()
        );
    }
}
