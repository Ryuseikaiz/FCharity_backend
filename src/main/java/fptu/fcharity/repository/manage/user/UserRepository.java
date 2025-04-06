package fptu.fcharity.repository.manage.user;

import fptu.fcharity.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @EntityGraph(attributePaths = {"walletAddress"})
    Optional<User> findByEmail(String email);
    @EntityGraph(attributePaths = {"walletAddress"})
    Optional<User> findByVerificationCode(String verificationCode);
    @Transactional
    @EntityGraph(attributePaths = {"walletAddress","walletAddress.balance"})
    @Query("SELECT u FROM User u WHERE u.id = :id")
    User findWithEssentialById(@Param("id") UUID id);

}