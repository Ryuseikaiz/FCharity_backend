package fptu.fcharity.repository.manage.post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import fptu.fcharity.entity.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {


    List<Comment> findByParentCommentCommentId(UUID parentCommentId);
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId And c.parentComment.commentId is null")
    Page<Comment> findByPost_Id(@Param("postId") UUID postId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    List<Comment> findAllCommentByPost_Id(@Param("postId") UUID postId);
    // CommentRepository.java
    @Query("SELECT c FROM Comment c WHERE c.parentComment.commentId = :parentCommentId")
    Page<Comment> findRepliesByParentId(@Param("parentCommentId") UUID parentCommentId, Pageable pageable);
    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT c FROM Comment c WHERE c.commentId = :commentId")
    Comment findEssentialById(UUID commentId);

}