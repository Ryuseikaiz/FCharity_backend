package fptu.fcharity.service.manage.post;

import fptu.fcharity.dto.post.PostUpdateDto;
import fptu.fcharity.entity.Post;
import fptu.fcharity.entity.User;
import fptu.fcharity.dto.post.PostRequestDTO;
import fptu.fcharity.repository.TagRepository;
import fptu.fcharity.response.post.PostResponse;
import fptu.fcharity.repository.manage.post.PostRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.repository.TaggableRepository;
import fptu.fcharity.service.ObjectAttachmentService;
import fptu.fcharity.service.TaggableService;
import fptu.fcharity.utils.constants.TaggableType;
import fptu.fcharity.utils.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
    private TaggableService taggableService;
    @Autowired
    private ObjectAttachmentService objectAttachmentService;


    // Lấy tất cả các Post
    public List<PostResponse> getAllPosts() {
        List<Post> posts = postRepository.findAllWithInclude();
        return posts.stream().map(post -> new PostResponse(post,
                taggableService.getTagsOfObject(post.getId(),TaggableType.POST),
                objectAttachmentService.getAttachmentsOfObject(post.getId(),TaggableType.POST))
                ).toList();
    }

    // Lấy Post theo ID
    public PostResponse getPostById(UUID postId) {
        Post post = postRepository.findWithIncludeById(postId);
        if(post == null){
            throw new ApiRequestException("Post not found");
        }
        return new PostResponse(post,
                taggableService.getTagsOfObject(post.getId(), TaggableType.POST),
                objectAttachmentService.getAttachmentsOfObject(post.getId(),TaggableType.POST));
    }
    // Tạo mới Post
    public PostResponse createPost(PostRequestDTO postRequestDTO) {
        // Lấy đối tượng User từ DB theo userId từ DTO
        Optional<User> optionalUser = userRepository.findById(postRequestDTO.getUserId());
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found with id: " + postRequestDTO.getUserId());
        }
        User user = optionalUser.get();
        Post post = new Post(user, postRequestDTO.getTitle(),postRequestDTO.getContent());

        Post savedPost = postRepository.save(post);
        taggableService.addTaggables(post.getId(), postRequestDTO.getTagIds(), TaggableType.POST);
        objectAttachmentService.saveAttachments(post.getId(), postRequestDTO.getImageUrls(), TaggableType.POST);
        objectAttachmentService.saveAttachments(post.getId(), postRequestDTO.getVideoUrls(), TaggableType.POST);

        return new PostResponse(savedPost,
                taggableService.getTagsOfObject(savedPost.getId(), TaggableType.POST),
                objectAttachmentService.getAttachmentsOfObject(savedPost.getId(),TaggableType.POST));
    }

    public PostResponse updatePost(UUID postId, PostUpdateDto postUpdateDTO) {
        Post post = postRepository.findWithIncludeById(postId);
        post.setTitle(postUpdateDTO.getTitle());
        post.setContent(postUpdateDTO.getContent());
        post.setVote(postUpdateDTO.getVote());

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

    // Xóa Post theo ID
    public void deletePost(UUID postId) {
        if (!postRepository.existsById(postId)) {
            throw new RuntimeException("Post not found with id: " + postId);
        }
        objectAttachmentService.clearAttachments(postId, TaggableType.POST);
        postRepository.deleteById(postId);
    }


}
