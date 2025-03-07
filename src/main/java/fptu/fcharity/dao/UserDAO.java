package fptu.fcharity.dao;

import fptu.fcharity.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserDAO {
    User findUserByEmail(String email);
    User getById(UUID id);
    List<User> getAllUsers();
}
