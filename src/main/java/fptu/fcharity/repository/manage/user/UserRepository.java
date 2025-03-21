package fptu.fcharity.repository.manage.user;

import fptu.fcharity.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationCode(String verificationCode);
    @EntityGraph(attributePaths = {"walletAddress"}) // Load thêm các quan hệ nếu cần
    @Query("SELECT u FROM User u WHERE u.id = :id")
    User findWithDetailsById(UUID id);
}