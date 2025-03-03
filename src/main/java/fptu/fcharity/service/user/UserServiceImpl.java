package fptu.fcharity.service.user;

import fptu.fcharity.dao.UserDAO;
import fptu.fcharity.entity.User;
import fptu.fcharity.exception.ApiRequestException;
import fptu.fcharity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserDAO userDAO) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDAO = userDAO;
    }

    @Override
    public List<User> findAllUsers() {
        return List.of();
    }

    @Override
    public User findUserByEmail(String email) {
        return userDAO.findUserByEmail(email);
    }

    @Override
    public User updatePassword(String email, String newPassword, String oldPassword) throws ApiRequestException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiRequestException("User not found"));
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new ApiRequestException("Old password is incorrect");
        }
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new ApiRequestException("New password must be different from the old password");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return user;
    }
}
