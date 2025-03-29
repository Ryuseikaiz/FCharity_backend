package fptu.fcharity.service.manage.user;

import fptu.fcharity.dto.authentication.ChangePasswordDto;
import fptu.fcharity.dto.project.ProjectRequestDto;
import fptu.fcharity.entity.OrganizationMember;
import fptu.fcharity.entity.ProjectRequest;
import fptu.fcharity.entity.TaskPlan;
import fptu.fcharity.entity.User;
import fptu.fcharity.repository.manage.organization.OrganizationMemberRepository;
import fptu.fcharity.repository.manage.organization.OrganizationRepository;
import fptu.fcharity.repository.manage.project.ProjectRepository;
import fptu.fcharity.repository.manage.project.ProjectRequestRepository;
import fptu.fcharity.repository.manage.project.TaskPlanRepository;
import fptu.fcharity.response.project.ProjectRequestResponse;
import fptu.fcharity.service.manage.project.TaskPlanService;
import fptu.fcharity.utils.exception.ApiRequestException;
import fptu.fcharity.repository.manage.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        if (!passwordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
            throw new ApiRequestException("Old password is incorrect");
        }
        if (passwordEncoder.matches(changePasswordDto.getNewPassword(), user.getPassword())) {
            throw new ApiRequestException("New password must be different from the old password");
        }
        updatePassword(user.getEmail(),passwordEncoder.encode(changePasswordDto.getNewPassword()));
        return user;
    }
    public List<User> getAllUsersNotInOrganization(UUID organizationId) {
        List<User> allUsers = userRepository.findAll();
        List<OrganizationMember> organizationMembers = organizationMemberRepository.findOrganizationMemberByOrganization(organizationRepository.findById(organizationId).orElseThrow(() -> new RuntimeException("Organization not found")));

        return allUsers.stream().filter(user -> {
            for (OrganizationMember organizationMember : organizationMembers) {
                if (organizationMember.getUser().getId() == user.getId()) {
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
}