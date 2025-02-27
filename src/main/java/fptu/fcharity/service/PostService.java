package fptu.fcharity.service;

import fptu.fcharity.entity.Post;
import fptu.fcharity.entity.User;
import fptu.fcharity.postdto.PostRequestDTO;
import fptu.fcharity.postdto.PostResponseDTO;
import fptu.fcharity.repository.PostRepository;
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

    // Lấy tất cả các Post
    public List<PostResponseDTO> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Lấy Post theo ID
    public Optional<PostResponseDTO> getPostById(UUID postId) {
        return postRepository.findById(postId).map(this::convertToDTO);
    }

    // Tạo mới Post
    public PostResponseDTO createPost(PostRequestDTO postRequestDTO) {
        // Lấy đối tượng User từ DB theo userId từ DTO
        Optional<User> optionalUser = userRepository.findById(postRequestDTO.getUserId());
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found with id: " + postRequestDTO.getUserId());
        }
        User user = optionalUser.get();

        // Tạo mới đối tượng Post và gán dữ liệu từ DTO
        Post post = new Post();
        post.setTitle(postRequestDTO.getTitle());
        post.setContent(postRequestDTO.getContent());
        post.setVote(postRequestDTO.getVote());
        post.setUser(user);

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

        // Cập nhật các thông tin của Post
        post.setTitle(postRequestDTO.getTitle());
        post.setContent(postRequestDTO.getContent());
        post.setVote(postRequestDTO.getVote());
        // Nếu cần cập nhật user, bạn có thể xử lý tương tự
        // Ở đây, ta giữ nguyên user hiện tại.

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

    // Phương thức chuyển đổi Post sang PostResponseDTO
    private PostResponseDTO convertToDTO(Post post) {
        PostResponseDTO dto = new PostResponseDTO();
        dto.setPostId(post.getPostId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setVote(post.getVote());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setUserId(post.getUser().getUserId());
        return dto;
    }
}
