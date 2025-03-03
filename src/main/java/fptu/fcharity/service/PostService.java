package fptu.fcharity.service;

import fptu.fcharity.entity.Post;
import fptu.fcharity.entity.Tag;
import fptu.fcharity.entity.User;
import fptu.fcharity.postdto.PostRequestDTO;
import fptu.fcharity.postdto.PostResponseDTO;
import fptu.fcharity.repository.PostRepository;
import fptu.fcharity.repository.TagRepository;
import fptu.fcharity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    // Lấy tất cả các Post
    public List<PostResponseDTO> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Lấy Post theo ID
    public Optional<PostResponseDTO> getPostById(UUID postId) {
        return postRepository.findById(postId)
                .map(this::convertToDTO);
    }

    // Tạo mới Post
    public PostResponseDTO createPost(PostRequestDTO postRequestDTO) {
        // Kiểm tra tồn tại của User
        Optional<User> optionalUser = userRepository.findById(postRequestDTO.getUserId());
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found with id: " + postRequestDTO.getUserId());
        }
        User user = optionalUser.get();

        // Kiểm tra danh sách tagIds
        List<UUID> tagIds = postRequestDTO.getTagIds();
        if (tagIds == null || tagIds.isEmpty()) {
            throw new RuntimeException("At least one tag is required.");
        }
        if (tagIds.size() > 5) {
            throw new RuntimeException("A post can have at most 5 tags.");
        }
        // Lấy danh sách Tag từ DB
        List<Tag> tags = tagRepository.findAllById(tagIds);
        if (tags.size() != tagIds.size()) {
            throw new RuntimeException("Some tags were not found.");
        }

        // Tạo mới đối tượng Post và gán dữ liệu từ DTO
        Post post = new Post();
        post.setTitle(postRequestDTO.getTitle());
        post.setContent(postRequestDTO.getContent());
        post.setVote(postRequestDTO.getVote());
        post.setUser(user);
        post.setTags(new HashSet<>(tags));

        Post savedPost = postRepository.save(post);
        return convertToDTO(savedPost);
    }

    // Cập nhật Post theo ID
    public PostResponseDTO updatePost(UUID postId, PostRequestDTO postRequestDTO) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new RuntimeException("Post not found with id: " + postId);
        }
        Post post = optionalPost.get();

        // Cập nhật các trường của Post
        post.setTitle(postRequestDTO.getTitle());
        post.setContent(postRequestDTO.getContent());
        post.setVote(postRequestDTO.getVote());
        // Nếu có thay đổi danh sách tag
        List<UUID> tagIds = postRequestDTO.getTagIds();
        if (tagIds != null) {
            if (tagIds.size() > 5) {
                throw new RuntimeException("A post can have at most 5 tags.");
            }
            List<Tag> tags = tagRepository.findAllById(tagIds);
            if (tags.size() != tagIds.size()) {
                throw new RuntimeException("Some tags were not found.");
            }
            post.setTags(new HashSet<>(tags));
        }
        // Giữ nguyên User hiện tại (hoặc xử lý cập nhật nếu cần)

        Post updatedPost = postRepository.save(post);
        return convertToDTO(updatedPost);
    }

    // Xóa Post theo ID
    public void deletePost(UUID postId) {
        if (!postRepository.existsById(postId)) {
            throw new RuntimeException("Post not found with id: " + postId);
        }
        postRepository.deleteById(postId);
    }

    // Chuyển đổi đối tượng Post thành PostResponseDTO
    private PostResponseDTO convertToDTO(Post post) {
        PostResponseDTO dto = new PostResponseDTO();
        dto.setPostId(post.getPostId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setVote(post.getVote());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setUserId(post.getUser().getUserId());
        List<UUID> tagIds = post.getTags()
                .stream()
                .map(Tag::getTagId)
                .collect(Collectors.toList());
        dto.setTagIds(tagIds);
        return dto;
    }
}
