package fptu.fcharity.dao;

import fptu.fcharity.entity.User;

public interface UserDAO {
    User findUserByEmail(String email);
}
