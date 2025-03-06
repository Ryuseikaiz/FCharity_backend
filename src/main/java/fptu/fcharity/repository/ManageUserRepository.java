package fptu.fcharity.repository;

import fptu.fcharity.entity.User;
import fptu.fcharity.entity.User.UserRole;
import fptu.fcharity.entity.User.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ManageUserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    List<User> findByUserStatus(UserStatus status);
    List<User> findByUserRole(UserRole role);
}
