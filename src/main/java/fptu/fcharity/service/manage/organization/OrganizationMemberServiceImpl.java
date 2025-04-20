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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fptu.fcharity.entity.OrganizationMember.OrganizationMemberRole;

import java.util.List;
import java.util.Objects;
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
    public List<OrganizationMemberDTO> findAll() {
        return organizationMemberRepository
                .findAll().stream()
                .map(organizationMemberMapper::toDTO).collect(Collectors.toList());
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
    public OrganizationMemberDTO findById(UUID id) {
        OrganizationMember organizationMember = organizationMemberRepository.findById(id).orElseThrow(() -> new ApiRequestException("Organization Member Not Found!"));
        return organizationMemberMapper.toDTO(organizationMember);
    }

    @Override
    public OrganizationMemberRole findUserRoleInOrganization(UUID userId, UUID organizationId) {
        return  organizationMemberRepository.findOrganizationMemberByUserIdAndOrganizationOrganizationId(userId, organizationId).getMemberRole();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationMemberDTO> findOrganizationMemberByOrganization(Organization organization) {
        return organizationMemberRepository
                .findOrganizationMemberByOrganization(organization)
                .stream()
                .map(organizationMemberMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationMemberDTO> findOrganizationMemberByOrganizationId(UUID organizationId) {
        Organization organization = organizationRepository.findById(organizationId).orElseThrow(() -> new ApiRequestException("organization not found"));
        List<OrganizationMemberDTO> result = organizationMemberRepository
                .findByOrganizationOrganizationId(organizationId)
                .stream()
                .map(organizationMemberMapper::toDTO)
                .collect(Collectors.toList());

        System.out.println("result 🧊🧊" + result);
        return result;
    }


    @Override
    @Transactional
    public OrganizationMemberDTO createOrganizationMember(UUID organizationId, UUID userId) {
        Organization organization = organizationRepository.findById(organizationId).orElseThrow(() -> new ApiRequestException("organization not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiRequestException("user not found"));

        OrganizationMember organizationMember = new OrganizationMember();

        if (organizationMemberRepository.findOrganizationMemberByUserIdAndOrganizationOrganizationId(userId, organizationId) != null)
            throw new ApiRequestException("Member already exists");

        organizationMember.setOrganization(organization);
        organizationMember.setUser(user);
        return organizationMemberMapper.toDTO(organizationMemberRepository.save(organizationMember));
    }

    @Override
    @Transactional
    public OrganizationMemberDTO updateRole(OrganizationMemberDTO organizationMemberDTO) {

        OrganizationMember currentOrganizationMemberInfo = organizationMemberRepository.findById(organizationMemberDTO.getMembershipId()).orElseThrow(()-> new ApiRequestException("Member not found"));
        User authUser  = userRepository.findByEmail(getAuthentication().getName()).orElseThrow(()-> new ApiRequestException("User not found"));

        if (!Objects.equals(organizationMemberDTO.getMemberRole(), currentOrganizationMemberInfo.getMemberRole())) {
            OrganizationMemberRole authRole = organizationMemberRepository.findOrganizationMemberByUserIdAndOrganizationOrganizationId(authUser.getId(), organizationMemberDTO.getOrganization().getOrganizationId()).getMemberRole();

            if (authRole == OrganizationMemberRole.CEO ) {
                currentOrganizationMemberInfo.setMemberRole(organizationMemberDTO.getMemberRole());
                return organizationMemberMapper.toDTO(organizationMemberRepository.save(currentOrganizationMemberInfo));
            }
            else if (authRole == OrganizationMemberRole.MANAGER) {
                if (currentOrganizationMemberInfo.getMemberRole() == OrganizationMemberRole.CEO || currentOrganizationMemberInfo.getMemberRole() == OrganizationMemberRole.MANAGER) {
                    throw new ApiRequestException("You are not allowed to update this role");
                } else {
                    currentOrganizationMemberInfo.setMemberRole(organizationMemberDTO.getMemberRole());
                    return organizationMemberMapper.toDTO(organizationMemberRepository.save(currentOrganizationMemberInfo));
                }
            } else
                throw new ApiRequestException("You are not allowed to update this role");
        }
        return organizationMemberMapper.toDTO(currentOrganizationMemberInfo);
    }

    @Override
    @Transactional
    public void delete(UUID membershipId) {
        OrganizationMember memberInfo = organizationMemberRepository.findById(membershipId).orElseThrow(()-> new ApiRequestException("Member not found"));

        User requestUser = userRepository.findByEmail(getAuthentication().getName()).orElseThrow(() -> new ApiRequestException("user not found"));
        OrganizationMemberRole requestUserRole = organizationMemberRepository.findOrganizationMemberByUserIdAndOrganizationOrganizationId(requestUser.getId(), memberInfo.getOrganization().getOrganizationId()).getMemberRole();


        if (requestUserRole == OrganizationMemberRole.CEO || requestUserRole == OrganizationMemberRole.MANAGER) {
            if (memberInfo.getMemberRole() != OrganizationMemberRole.CEO) {
                organizationMemberRepository.deleteById(membershipId);
                OrganizationRequest orgRequest = organizationRequestRepository.findByOrganizationOrganizationIdAndUserId(memberInfo.getOrganization().getOrganizationId(), memberInfo.getUser().getId());
                if (orgRequest != null) {
                    organizationRequestRepository.deleteById(orgRequest.getOrganizationRequestId());
                }
            }
        } else {
            throw new ApiRequestException("user does not have permission to delete member");
        }
    }

    public void updateMemberRole(OrganizationMember organizationMember, UUID id) {}

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
