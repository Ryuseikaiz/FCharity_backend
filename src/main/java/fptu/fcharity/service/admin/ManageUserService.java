package fptu.fcharity.service.admin;

import fptu.fcharity.dto.admindashboard.UserDTO;
import fptu.fcharity.entity.User;
import fptu.fcharity.entity.User.UserStatus;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.utils.exception.ApiRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManageUserService {
    private final UserRepository userRepository;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }


    public UserDTO getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiRequestException("User not found with ID: " + userId));
        return convertToDTO(user);
    }


    @Transactional
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiRequestException("User not found"));
        userRepository.delete(user);
    }

    @Transactional
    public void banUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiRequestException("User not found with ID: " + userId));

        user.setUserStatus(UserStatus.Banned);
        userRepository.save(user);
    }

    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getAddress(),
                user.getAvatar(),
                user.getUserRole(),
                user.getUserStatus(),
                user.getCreatedDate()
        );
    }
}
