package fptu.fcharity.service.manage.organization;

import fptu.fcharity.dto.organization.OrganizationMemberDTO;
import fptu.fcharity.dto.organization.UserDTO;
import fptu.fcharity.entity.Organization;
import fptu.fcharity.entity.OrganizationMember;
import fptu.fcharity.entity.OrganizationRequest;
import fptu.fcharity.entity.User;
import fptu.fcharity.repository.manage.organization.OrganizationMemberRepository;
import fptu.fcharity.repository.manage.organization.OrganizationRepository;
import fptu.fcharity.repository.manage.organization.OrganizationRequestRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.utils.exception.ApiRequestException;
import fptu.fcharity.utils.mapper.organization.OrganizationMemberMapper;
import fptu.fcharity.utils.mapper.organization.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fptu.fcharity.entity.OrganizationMember.OrganizationMemberRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrganizationMemberServiceImpl implements OrganizationMemberService {
    private final OrganizationMemberRepository organizationMemberRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final OrganizationRequestRepository organizationRequestRepository;
    private final OrganizationMemberMapper organizationMemberMapper;
    private final UserMapper userMapper;

    @Autowired
    public OrganizationMemberServiceImpl(
            OrganizationMemberRepository organizationMemberRepository,
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            OrganizationRequestRepository organizationRequestRepository,
            OrganizationMemberMapper organizationMemberMapper, UserMapper userMapper) {
        this.organizationMemberRepository = organizationMemberRepository;
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.organizationRequestRepository = organizationRequestRepository;
        this.organizationMemberMapper = organizationMemberMapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationMember> findAll() {
        return organizationMemberRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsersNotInOrganization(UUID organizationId) {
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
        }).map(userMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<OrganizationMember> findById(UUID id) {
        return organizationMemberRepository.findOrganizationMemberByMembershipId(id);
    }

    @Override
    public OrganizationMemberRole findUserRoleInOrganization(UUID userId, UUID organizationId) {
        return  organizationMemberRepository.findOrganizationMemberByUserIdAndOrganizationOrganizationId(userId, organizationId).getMemberRole();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationMember> findOrganizationMemberByOrganization(Organization organization) {
        return organizationMemberRepository.findOrganizationMemberByOrganization(organization);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationMemberDTO> findOrganizationMemberByOrganizationId(UUID organizationId) {
        Organization organization = organizationRepository.findById(organizationId).orElseThrow(() -> new ApiRequestException("organization not found"));
        return organizationMemberRepository
                .findByOrganizationOrganizationId(organizationId)
                .stream()
                .map(organizationMemberMapper::toDTO)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public OrganizationMember save(OrganizationMember organizationMember) {
        return organizationMemberRepository.save(organizationMember);
    }

    @Override
    @Transactional
    public OrganizationMember update(OrganizationMember organizationMember) {
        return organizationMemberRepository.save(organizationMember);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        organizationMemberRepository.deleteById(id);
    }

    public void updateMemberRole(OrganizationMember organizationMember, UUID id) {}
}
