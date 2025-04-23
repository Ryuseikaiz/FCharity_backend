package fptu.fcharity.repository.manage.user;

import fptu.fcharity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationCode(String verificationCode);
    @Transactional
    @Query("SELECT u FROM User u WHERE u.id = :id")
    User findWithEssentialById(@Param("id") UUID id);
    @Query("SELECT u FROM User u")
    List<User> findAllWithInclude();

    Optional<Object> getUserById(UUID id);
}