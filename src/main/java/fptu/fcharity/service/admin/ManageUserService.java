package fptu.fcharity.service.admin;

import fptu.fcharity.dto.admindashboard.ReasonDTO;
import fptu.fcharity.dto.admindashboard.UserDTO;
import fptu.fcharity.entity.User;
import fptu.fcharity.entity.User.UserStatus;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.service.authentication.AuthenticationService;
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
    private final AuthenticationService authenticationService;

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
    public void banUser(UUID userId, ReasonDTO reasonDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiRequestException("User not found with ID: " + userId));

        user.setUserStatus(UserStatus.Banned);
        user.setReason(reasonDTO.getReason());
        userRepository.save(user);
        authenticationService.sendBanNotificationEmail(user, reasonDTO.getReason());
    }

    @Transactional
    public void unbanUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiRequestException("User not found with ID: " + userId));

        if (!UserStatus.Banned.equals(user.getUserStatus())) {
            throw new ApiRequestException("Only banned users can be unbanned.");
        }

        user.setUserStatus(UserStatus.Verified);
        user.setReason(null);
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
                user.getCreatedDate(),
                user.getReason());
    }
}
