package fptu.fcharity.service;

import fptu.fcharity.dto.authentication.ChangePasswordDto;
import fptu.fcharity.entity.User;
import fptu.fcharity.exception.ApiRequestException;
import fptu.fcharity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public List<User> allUsers() {
        return userRepository.findAll();
    }
    public User findUserByEmail(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.orElse(null);
    }
    public void updatePassword(String email, String password) {
        User user = userRepository.findByEmail(email).get();
        user.setPassword(password);
        userRepository.save(user);
    }
    public User changePassword(ChangePasswordDto changePasswordDto) {
        User user = userRepository.findByEmail(changePasswordDto.getEmail())
                .orElseThrow(() -> new ApiRequestException("User not found"));
        if (!passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
            throw new ApiRequestException("Old password is incorrect");
        }
        if (passwordEncoder.matches(changePasswordDto.getNewPassword(), user.getPassword())) {
            throw new ApiRequestException("New password must be different from the old password");
        }
        updatePassword(user.getEmail(),passwordEncoder.encode(changePasswordDto.getNewPassword()));
        return user;
    }
}