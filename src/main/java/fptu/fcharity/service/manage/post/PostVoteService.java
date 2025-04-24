package fptu.fcharity.service.manage.post;

import fptu.fcharity.entity.*;
import fptu.fcharity.repository.manage.post.PostRepository;
import fptu.fcharity.repository.manage.post.PostVoteRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.utils.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class PostVoteService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PostVoteRepository postVoteRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void votePost(UUID postId, UUID userId, int newVote) {
        if (newVote != 1 && newVote != -1 && newVote != 0) {
            throw new IllegalArgumentException("Vote must be 1 (upvote), -1 (downvote), or 0 (unvote)");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiRequestException("Post not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiRequestException("User not found"));

        PostVoteId voteId = new PostVoteId(postId, userId);
        Optional<PostVote> existingVote = postVoteRepository.findById(voteId);

        if (existingVote.isPresent()) {
            PostVote voteRecord = existingVote.get();

            if (newVote == 0) {
                // Unvote
                postVoteRepository.delete(voteRecord);
            } else if (voteRecord.getVote() != newVote) {
                // Đổi vote
                voteRecord.setVote(newVote);
                postVoteRepository.save(voteRecord);
            }
        } else if (newVote != 0) {
            // Vote mới
            PostVote newVoteRecord = new PostVote();
            newVoteRecord.setId(voteId);
            newVoteRecord.setPost(post);
            newVoteRecord.setUser(user);
            newVoteRecord.setVote(newVote);
            postVoteRepository.save(newVoteRecord);
        }

        // Cập nhật tổng vote
        int totalVotes = postVoteRepository.sumVotesByPostId(postId);
        post.setVote(totalVotes);
        postRepository.save(post);
    }
    public int getTotalVotes(UUID postId) {
        return postVoteRepository.sumVotesByPostId(postId);
    }


}