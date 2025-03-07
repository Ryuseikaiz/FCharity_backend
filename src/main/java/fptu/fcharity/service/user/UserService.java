package fptu.fcharity.service.user;

import fptu.fcharity.entity.User;
import fptu.fcharity.exception.ApiRequestException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    List<User> findAllUsers();
    Optional<User> findUserByEmail(String email);
    User updatePassword(String email, String newPassword, String oldPassword) throws ApiRequestException;
    Optional<User> getById(UUID id);
    List<User> getAllUsers();
}
