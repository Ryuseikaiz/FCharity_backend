package fptu.fcharity.service.manageuser;

import fptu.fcharity.dto.admindashboard.UserDTO;
import fptu.fcharity.entity.User;
import fptu.fcharity.entity.User.UserStatus;
import fptu.fcharity.repository.ProjectMemberRepository;
import fptu.fcharity.utils.exception.ApiRequestException;
import fptu.fcharity.repository.ManageUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManageUserService {
    private final ManageUserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;
    // Lấy danh sách user
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Lấy user theo ID
    public UserDTO getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiRequestException("User not found with ID: " + userId));
        return convertToDTO(user);
    }

    // Xóa user
    @Transactional
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiRequestException("User not found"));

        // Xóa tất cả các bản ghi trong project_members liên quan đến user này
        projectMemberRepository.deleteByUserId(userId);

        // Xóa user
        userRepository.delete(user);
    }

//    // Duyệt user lên Founder
//    @Transactional
//    public void approveUserToFounder(UUID userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
//
//        user.setUserRole(UserRole.Founder);
//        userRepository.save(user);
//    }

    // Ban user
    @Transactional
    public void banUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiRequestException("User not found with ID: " + userId));

        user.setUserStatus(UserStatus.Banned);
        userRepository.save(user);
    }


    // Chuyển đổi từ entity sang DTO
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
