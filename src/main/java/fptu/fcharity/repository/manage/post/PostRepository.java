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
    @EntityGraph(attributePaths = {"user"})
    Post findWithIncludeById(UUID id);
    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT r FROM Post r")
    List<Post> findAllWithInclude();
    List<Post> findByUserId(UUID userId);

}