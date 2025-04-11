package fptu.fcharity.repository.manage.post;

import fptu.fcharity.entity.PostVote;
import fptu.fcharity.entity.PostVoteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.UUID;

@Repository
public interface PostVoteRepository extends JpaRepository<PostVote, PostVoteId> {

    @Query("SELECT COALESCE(SUM(pv.vote), 0) FROM PostVote pv WHERE pv.post.id = :postId")
    int sumVotesByPostId(@Param("postId") UUID postId);

}

