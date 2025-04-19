package fptu.fcharity.service.manage.post;

import fptu.fcharity.dto.post.CommentDTO;
import fptu.fcharity.entity.*;
import fptu.fcharity.repository.manage.post.CommentRepository;
import fptu.fcharity.repository.manage.post.CommentVoteRepository;
import fptu.fcharity.repository.manage.post.PostRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.response.authentication.UserResponse;
import fptu.fcharity.response.post.CommentFinalResponse;
import fptu.fcharity.response.post.CommentResponse;
import fptu.fcharity.utils.mapper.UserResponseMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentVoteRepository commentVoteRepository;

    @Autowired
    private UserResponseMapper userResponseMapper;
    public CommentResponse createComment(CommentDTO commentDTO) {
        Comment comment = convertToEntity(commentDTO);
        comment = commentRepository.save(comment);
        Comment e = commentRepository.findEssentialById(comment.getCommentId());
        CommentResponse commentResponse = convertToResponse(e);
        return commentResponse;
    }

    public List<CommentResponse> getAllCommentsByPostId(UUID postId){
        List<Comment> l = commentRepository.findAllCommentByPost_Id(postId);
        return l.stream().map(this::convertToResponse).toList();
    }
    public List<CommentFinalResponse> getCommentsByPost(UUID postId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Comment> commentPage = commentRepository.findByPost_Id(postId, pageable);
        List<CommentResponse> l = commentPage.getContent().stream().map(this::convertToResponse).toList();
        List<CommentFinalResponse> finalList = new ArrayList<>();
        for(CommentResponse commentResponse : l) {
            List<Comment> repliesList = commentRepository.findByParentCommentCommentId(commentResponse.getCommentId());
            if(repliesList.isEmpty()) {
                finalList.add(new CommentFinalResponse(commentResponse, new ArrayList<>()));
                continue;
            }
            List<CommentResponse> repliesResponseList = repliesList.stream().map(this::convertToResponse).toList();
            CommentFinalResponse a = new CommentFinalResponse(commentResponse, repliesResponseList);
            finalList.add(a);
        }
        return finalList;
    }

    public CommentResponse updateComment(UUID commentId, CommentDTO commentDTO) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        comment.setContent(commentDTO.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);
        return convertToResponse(comment);
    }

    public void deleteComment(UUID commentId) {
        commentRepository.deleteById(commentId);
    }

    public CommentResponse createReply(UUID parentCommentId, CommentDTO commentDTO) {
        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));

        Comment reply = convertToEntity(commentDTO);
        reply.setParentComment(parentComment);
        return convertToResponse(commentRepository.save(reply));
    }

    @Transactional
    public void voteComment(UUID commentId, UUID userId, int newVote) {
        if (newVote != 1 && newVote != -1 && newVote != 0) {
            throw new IllegalArgumentException("Vote must be 1 (upvote), -1 (downvote), or 0 (unvote)");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        CommentVoteId voteId = new CommentVoteId(commentId, userId);
        Optional<CommentVote> existingVote = commentVoteRepository.findById(voteId);

        if (existingVote.isPresent()) {
            CommentVote voteRecord = existingVote.get();
            if (newVote == 0 || voteRecord.getVote() == newVote) {
                commentVoteRepository.delete(voteRecord); // Unvote
            } else {
                voteRecord.setVote(newVote); // Cập nhật vote
                commentVoteRepository.save(voteRecord);
            }
        } else if (newVote != 0) {
            CommentVote newVoteRecord = new CommentVote(voteId, comment, user, newVote);
            commentVoteRepository.save(newVoteRecord);
        }

        int totalVotes = commentVoteRepository.sumVotesByCommentId(commentId);
        comment.setVote(totalVotes);
        commentRepository.save(comment);
    }

    private Comment convertToEntity(CommentDTO commentDTO) {
        Post post = postRepository.findById(commentDTO.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        User user = userRepository.findById(commentDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return Comment.builder()
                .post(post)
                .user(user)
                .content(commentDTO.getContent())
                .vote(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .parentComment(commentDTO.getParentCommentId() != null ?
                        commentRepository.findById(commentDTO.getParentCommentId()).orElse(null) : null)
                .build();
    }

    private CommentResponse convertToResponse(Comment comment) {
        return CommentResponse.builder()
                .commentId(comment.getCommentId())
                .postId(comment.getPost().getId())
                .user(userResponseMapper.toDTO(comment.getUser()))
                .content(comment.getContent())
                .vote(comment.getVote())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .parentCommentId(comment.getParentComment()!=null ? comment.getParentComment().getCommentId():null)
                .build();
    }

    public Comment getCommentById(UUID commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
    }
}