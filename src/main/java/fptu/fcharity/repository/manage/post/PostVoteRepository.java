package fptu.fcharity.repository.manage.post;

import fptu.fcharity.entity.Post;
import fptu.fcharity.entity.PostVote;
import fptu.fcharity.entity.PostVoteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface PostVoteRepository extends JpaRepository<PostVote, PostVoteId> {
}
