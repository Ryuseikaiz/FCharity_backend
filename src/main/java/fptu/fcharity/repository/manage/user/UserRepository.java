package fptu.fcharity.repository.manage.user;

import fptu.fcharity.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @EntityGraph(attributePaths = {"walletAddress"})
    Optional<User> findByEmail(String email);
    @EntityGraph(attributePaths = {"walletAddress"})
    Optional<User> findByVerificationCode(String verificationCode);
    @EntityGraph(attributePaths = {"walletAddress"})
    @Query("SELECT u FROM User u WHERE u.id = :id")
    User findWithDetailsById(@Param("id") UUID id);
}