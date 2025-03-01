package fptu.fcharity.service;

import fptu.fcharity.entity.Post;
import fptu.fcharity.entity.Tag;
import fptu.fcharity.entity.Taggable;
import fptu.fcharity.entity.User;
import fptu.fcharity.dto.post.PostRequestDTO;
import fptu.fcharity.repository.TagRepository;
import fptu.fcharity.response.post.PostResponse;
import fptu.fcharity.repository.PostRepository;
import fptu.fcharity.repository.UserRepository;
import fptu.fcharity.repository.TaggableRepository;
import fptu.fcharity.utils.constants.TaggableType;
import fptu.fcharity.utils.exception.ApiRequestException;
import fptu.fcharity.utils.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    @Autowired
    private TaggableRepository taggableRepository;

    @Autowired
    private PostMapper postMapper;

    public void addPostTags(UUID requestId, List<UUID> tagIds) {
        Post post = postRepository.findById(requestId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()).getBody();
        if (post != null) {
            for (UUID tagId : tagIds) {
                if (tagRepository.existsById(tagId)) {
                    Tag tag = tagRepository.findById(tagId)
                            .orElseThrow(() -> new ApiRequestException("Tag not found"));
                    Taggable taggable = new Taggable(tag,requestId, TaggableType.POST);
                    taggableRepository.save(taggable);
                }
            }
        }}
    public void updatePostTags(UUID postId, List<UUID> tagIds) {
        List<Taggable> oldTags = taggableRepository.findAllWithInclude().stream()
                .filter(taggable -> taggable.getTaggableId().equals(postId) && taggable.getTaggableType().equals(TaggableType.POST))
                .toList();
        for (Taggable taggable: oldTags) {
            if(!tagIds.contains(taggable.getTag().getId())){taggableRepository.deleteById(taggable.getId());}
            tagIds.remove(taggable.getTag().getId());
        }
        addPostTags(postId, tagIds);
    }
    // Lấy tất cả các Post
    public List<PostResponse> getAllPosts() {
        List<Post> posts = postRepository.findAllWithInclude();
        return posts.stream().map(post -> postMapper.convertToDTO(post, getTagsOfPost(post.getId()))).collect(Collectors.toList());
    }

    // Lấy Post theo ID
    public PostResponse getPostById(UUID postId) {
        Post post = postRepository.findWithIncludeById(postId);
        return postMapper.convertToDTO(post, getTagsOfPost(postId));
    }
    public List<Taggable> getTagsOfPost(UUID postId) {
        return taggableRepository.findAllWithInclude().stream()
                .filter(taggable -> taggable.getTaggableId().equals(postId) && taggable.getTaggableType().equals(TaggableType.POST))
                .toList();
    }
    // Tạo mới Post
    public PostResponse createPost(PostRequestDTO postRequestDTO) {
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
        addPostTags(post.getId(), postRequestDTO.getTagIds());
        return  postMapper.convertToDTO(savedPost, getTagsOfPost(savedPost.getId()));
    }

    public PostResponse updatePost(UUID postId, PostRequestDTO postRequestDTO) {
        Post post = postRepository.findWithIncludeById(postId);
        post.setTitle(postRequestDTO.getTitle());
        post.setContent(postRequestDTO.getContent());
        post.setVote(postRequestDTO.getVote());

        Post updatedPost = postRepository.save(post);
        updatePostTags(post.getId(), postRequestDTO.getTagIds());
        return postMapper.convertToDTO(updatedPost, getTagsOfPost(updatedPost.getId()));
    }

    // Xóa Post theo ID
    public void deletePost(UUID postId) {
        if (!postRepository.existsById(postId)) {
            throw new RuntimeException("Post not found with id: " + postId);
        }
        postRepository.deleteById(postId);
    }


}
