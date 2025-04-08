package fptu.fcharity.service.manage.user;

import fptu.fcharity.dto.authentication.ChangePasswordDto;
import fptu.fcharity.dto.project.ProjectRequestDto;
import fptu.fcharity.entity.*;
import fptu.fcharity.repository.TransactionHistoryRepository;
import fptu.fcharity.repository.WalletRepository;
import fptu.fcharity.repository.manage.organization.OrganizationMemberRepository;
import fptu.fcharity.repository.manage.organization.OrganizationRepository;
import fptu.fcharity.repository.manage.organization.OrganizationRequestRepository;
import fptu.fcharity.repository.manage.project.ProjectMemberRepository;
import fptu.fcharity.repository.manage.project.ProjectRepository;
import fptu.fcharity.repository.manage.project.ProjectRequestRepository;
import fptu.fcharity.repository.manage.project.TaskPlanRepository;
import fptu.fcharity.response.authentication.UserResponse;
import fptu.fcharity.response.project.ProjectRequestResponse;
import fptu.fcharity.response.user.TransactionHistoryResponse;
import fptu.fcharity.service.manage.project.TaskPlanService;
import fptu.fcharity.utils.constants.TransactionType;
import fptu.fcharity.utils.constants.project.ProjectRequestStatus;
import fptu.fcharity.utils.exception.ApiRequestException;
import fptu.fcharity.repository.manage.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import fptu.fcharity.dto.user.UpdateProfileDto;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private OrganizationMemberRepository organizationMemberRepository;
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private TaskPlanRepository taskPlanRepository;
    @Autowired
    private ProjectRequestRepository projectRequestRepository;
    @Autowired
    private OrganizationRequestRepository organizationRequestRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectMemberRepository projectMemberRepository;;

    public List<User> allUsers() {
        return userRepository.findAll();
    }
    @Transactional(readOnly = true)
    public Optional<User> getById(UUID id) {
        return userRepository.findById(id);
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

        // Nếu user đã có mật khẩu, kiểm tra oldPassword
        if (user.getPassword() != null) {
            if (!passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
                throw new ApiRequestException("Old password is incorrect");
            }
            if (passwordEncoder.matches(changePasswordDto.getNewPassword(), user.getPassword())) {
                throw new ApiRequestException("New password must be different from the old password");
            }
        } else {
            // Nếu user chưa có mật khẩu, có thể kiểm tra nếu raw oldPassword không rỗng
            if (changePasswordDto.getOldPassword() != null && !changePasswordDto.getOldPassword().isEmpty()) {
                throw new ApiRequestException("User has no password set yet");
            }
        }

        updatePassword(user.getEmail(), passwordEncoder.encode(changePasswordDto.getNewPassword()));
        return user;
    }

    public List<User> getAllUsersNotInOrganization(UUID organizationId) {
        List<User> allUsers = userRepository.findAll();
        List<OrganizationMember> organizationMembers = organizationMemberRepository.findAllOrganizationMemberByOrganization(organizationId);
        List<OrganizationRequest> organizationRequests = organizationRequestRepository.findByOrganizationOrganizationIdAndRequestType(organizationId, OrganizationRequest.OrganizationRequestType.Invitation).stream().filter(organizationRequest -> organizationRequest.getStatus() != OrganizationRequest.OrganizationRequestStatus.Rejected).toList();
        return allUsers.stream().filter(user -> {
            for (OrganizationMember organizationMember : organizationMembers) {
                if (organizationMember.getUser().getId() == user.getId()) {
                    return false;
                }
            }
            for(OrganizationRequest organizationRequest : organizationRequests) {
                if (organizationRequest.getUser().getId() == user.getId()) {
                    return false;
                }
            }
            return true;
        }).toList();
    }
    //get invitation from project of user
    public List<ProjectRequestResponse> getInvitationsOfUserId(UUID userId) {
        List<ProjectRequest> l = projectRequestRepository.findWithEssentialByUserId(userId);
        return l.stream().map(ProjectRequestResponse::new).toList();
    }

    //get all tasks of user
    //get all task of project id
    public List<TaskPlan> getTasksOfUserId(UUID userId) {
        return taskPlanRepository.findTaskPlanByUser(userId);
    }
    public List<TaskPlan> getTasksOfProjectId(UUID userId) {
        return taskPlanRepository.findTaskPlanByProject(userId);
    }
    public User updateProfile(UUID userId, UpdateProfileDto updateProfileDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiRequestException("User not found"));
        // Cập nhật các trường
        user.setFullName(updateProfileDto.getFullName());
        user.setPhoneNumber(updateProfileDto.getPhoneNumber());
        user.setAddress(updateProfileDto.getFullAddress());
        user.setAvatar(updateProfileDto.getAvatar());
        return userRepository.save(user);
    }
    public void updateVerificationCode(UUID userId, String code) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiRequestException("User not found"));
        // Cập nhật các trường
        user.setVerificationCode(code);
         userRepository.save(user);
    }

    //wallet process
    //deposit
    public void depositToWallet(String code, BigDecimal amount, Instant transactionDateTime) {
        User user = userRepository.findByVerificationCode(code)
                .orElseThrow(() -> new ApiRequestException("User find by code not found"));
        Wallet wallet = user.getWalletAddress();
        wallet.setBalance(wallet.getBalance().add(amount));
        TransactionHistory transactionHistory = new TransactionHistory(
                wallet,
                amount,
                TransactionType.DEPOSIT,
                transactionDateTime,
                wallet
        );
        transactionHistoryRepository.save(transactionHistory);
        walletRepository.save(wallet);
        user.setVerificationCode(null);
        userRepository.save(user);
    }

    public List<TransactionHistoryResponse> getTransactionHistoryOfUserId(UUID userId) {
        User user = userRepository.findWithEssentialById(userId);
        List<TransactionHistory> l = transactionHistoryRepository.findTransactionHistoryByWalletId(user.getWalletAddress().getId());
        return l.stream().map(m -> {
            if ("DONATE_PROJECT".equals(m.getTransactionType())) {
                Project p = projectRepository.findByWalletAddressId(m.getTargetWallet().getId());
                return new TransactionHistoryResponse(m, p.getId(), p.getProjectName());
            } else if ("DEPOSIT".equals(m.getTransactionType())) {
                return new TransactionHistoryResponse(m, null, null);
            } else {
                return new TransactionHistoryResponse(m, null, null); // fallback for other types
            }
        }).toList();
    }

    public List<UserResponse> getUsersNotInProject(UUID projectId) {
        List<User> allUsers = userRepository.findAllWithInclude();
        List<ProjectMember> projectMembers = projectMemberRepository.findByProjectId(projectId);
        List<User> invitedUser = projectRequestRepository.findWithEssentialByProjectId(projectId).stream()
                .filter(projectRequest -> !Objects.equals(projectRequest.getStatus(), ProjectRequestStatus.REJECTED))
                .map(ProjectRequest::getUser)
                .toList();
        List<User> usersNotInProject = allUsers.stream()
                .filter(user -> projectMembers.stream().noneMatch(projectMember -> projectMember.getUser().getId().equals(user.getId())))
                .filter(user -> invitedUser.stream().noneMatch(invited -> invited.getId().equals(user.getId())))
                .toList();
        return usersNotInProject.stream().map(UserResponse::new).toList();
}
}