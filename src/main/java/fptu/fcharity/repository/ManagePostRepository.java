package fptu.fcharity.repository;

import fptu.fcharity.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ManagePostRepository extends JpaRepository<Post, UUID> {
    List<Post> findByPostStatus(String status);
    Optional<Post> findById(UUID postId);
}
