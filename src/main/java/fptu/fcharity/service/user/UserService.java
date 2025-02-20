package fptu.fcharity.service.user;

import fptu.fcharity.entity.User;
import fptu.fcharity.exception.ApiRequestException;

import java.util.List;

public interface UserService {
    List<User> findAllUsers();
    User findUserByEmail(String email);
    User updatePassword(String email, String newPassword, String oldPassword) throws ApiRequestException;
}
