package fptu.fcharity.service.user;

import fptu.fcharity.entity.OrganizationMember;
import fptu.fcharity.entity.User;

import fptu.fcharity.repository.OrganizationRequestRepository;
import fptu.fcharity.repository.manage.organization.OrganizationMemberRepository;
import fptu.fcharity.repository.manage.organization.OrganizationRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.utils.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrganizationRequestRepository organizationRequestRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, OrganizationMemberRepository organizationMemberRepository, OrganizationRepository organizationRepository, OrganizationRequestRepository organizationRequestRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.organizationMemberRepository = organizationMemberRepository;
        this.organizationRepository = organizationRepository;
        this.organizationRequestRepository = organizationRequestRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
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

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }


    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> findAllUsersNotInOrganization(UUID organizationId) {
        List<User> allUsers = userRepository.findAll();
        List<OrganizationMember> organizationMembers = organizationMemberRepository.findOrganizationMemberByOrganization(organizationRepository.findById(organizationId).orElseThrow(() -> new RuntimeException("Organization not found")));

        List<User> result =  allUsers.stream().filter(user -> {
            for (OrganizationMember organizationMember : organizationMembers) {
                if (organizationMember.getUser().getUserId() == user.getUserId()) {
                    return false;
                }
            }
            return true;
        }).filter(user -> {
            return organizationRequestRepository.findByUserUserIdAndOrganizationOrganizationId(user.getUserId(), organizationId) == null;
        }).toList();

        return result;
    }
}
