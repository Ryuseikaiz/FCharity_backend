package fptu.fcharity.repository.manage.post;

import fptu.fcharity.entity.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    @EntityGraph(attributePaths = {"user","user.walletAddress"})
    Post findWithIncludeById(UUID id);
    @EntityGraph(attributePaths = {"user","user.walletAddress"})
    @Query("SELECT r FROM Post r")
    List<Post> findAllWithInclude();
    @Query("SELECT DISTINCT p FROM Post p " +
            "JOIN Taggable tg ON tg.objectId = p.id AND tg.objectType = 'POST' " +
            "JOIN Tag t ON t.id = tg.tag.id " +
            "WHERE LOWER(t.tagName) = LOWER(:tagName)")
    List<Post> findPostsByTagName(@org.springframework.data.repository.query.Param("tagName") String tagName);
}
