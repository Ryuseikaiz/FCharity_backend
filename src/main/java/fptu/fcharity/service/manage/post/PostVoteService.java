package fptu.fcharity.service.manage.post;

import fptu.fcharity.entity.Post;
import fptu.fcharity.entity.PostVote;
import fptu.fcharity.entity.PostVoteId;
import fptu.fcharity.entity.User;
import fptu.fcharity.repository.manage.post.PostRepository;
import fptu.fcharity.repository.manage.post.PostVoteRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.utils.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
@Service
public class PostVoteService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    PostVoteRepository postVoteRepository;
    @Autowired
    private UserRepository userRepository;
    @Transactional
    public void votePost(UUID postId, UUID userId, int voteValue) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new ApiRequestException("Post not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiRequestException("User not found"));

        PostVoteId voteId = new PostVoteId();
        voteId.setPostId(postId);
        voteId.setUserId(userId);

        Optional<PostVote> existingVote = postVoteRepository.findById(voteId);

        if (existingVote.isPresent()) {
            PostVote vote = existingVote.get();

            // Nếu user bấm lại vote cũ => Hủy vote (đặt lại 0 hoặc xóa)
            if (vote.getVote() == voteValue) {
                postVoteRepository.delete(vote);
                post.setVote(post.getVote() - voteValue); // Trừ điểm bài viết
            } else {
                vote.setVote(voteValue);
                vote.setUpdatedAt(Instant.now());
                postVoteRepository.save(vote);

                post.setVote(post.getVote() + (voteValue * 2)); // Đảo chiều vote (VD: -1 → 1)
            }
        } else {
            PostVote newVote = new PostVote();
            newVote.setId(voteId);
            newVote.setPost(post);
            newVote.setUser(user);
            newVote.setVote(voteValue);
            newVote.setCreatedAt(Instant.now());
            newVote.setUpdatedAt(Instant.now());

            postVoteRepository.save(newVote);
            post.setVote(post.getVote() + voteValue); // Cộng vote mới vào tổng điểm
        }

        postRepository.save(post);
    }

    @Transactional
    public void unvotePost(UUID postId, UUID userId) {
        PostVoteId voteId = new PostVoteId();
        voteId.setPostId(postId);
        voteId.setUserId(userId);

        Optional<PostVote> existingVote = postVoteRepository.findById(voteId);
        if (existingVote.isPresent()) {
            PostVote vote = existingVote.get();
            int previousVote = vote.getVote();

            postVoteRepository.delete(vote);

            Post post = vote.getPost();
            post.setVote(post.getVote() - previousVote); // Trừ điểm bài viết
            postRepository.save(post);
        }
    }
}
