        package fptu.fcharity.repository.manage.post;

import fptu.fcharity.entity.CommentVote;
import fptu.fcharity.entity.CommentVoteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentVoteRepository extends JpaRepository<CommentVote, CommentVoteId> {

    // Tìm vote theo comment và user
    Optional<CommentVote> findByCommentCommentIdAndUserId(UUID commentId, UUID userId);
    @Query("""
    SELECT COALESCE(SUM(cv.vote), 0)
    FROM CommentVote cv
    WHERE cv.comment.commentId = :commentId
       OR cv.comment.parentComment.commentId = :commentId
""")
    int sumVotesIncludingReplies(@Param("commentId") UUID commentId);

    @Query("SELECT COALESCE(SUM(cv.vote), 0) FROM CommentVote cv WHERE cv.comment.commentId = :commentId")
    int sumVotesByCommentId(@Param("commentId") UUID commentId);}