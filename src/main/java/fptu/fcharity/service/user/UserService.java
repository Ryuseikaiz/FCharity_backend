package fptu.fcharity.service.user;

import fptu.fcharity.entity.User;
import fptu.fcharity.utils.exception.ApiRequestException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    List<User> findAll();
    List<User> findAllUsersNotInOrganization(UUID organizationId);

    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);

    User updatePassword(String email, String newPassword, String oldPassword) throws ApiRequestException;

}
