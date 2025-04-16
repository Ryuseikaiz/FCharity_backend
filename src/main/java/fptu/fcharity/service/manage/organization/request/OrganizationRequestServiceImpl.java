package fptu.fcharity.service.manage.organization.request;

import fptu.fcharity.dto.organization.OrganizationRequestDTO;
import fptu.fcharity.dto.request.OrganizationRequestDto;
import fptu.fcharity.entity.*;

import fptu.fcharity.repository.manage.organization.*;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.service.HelpNotificationService;
import fptu.fcharity.utils.exception.ApiRequestException;
import fptu.fcharity.utils.mapper.organization.OrganizationRequestMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrganizationRequestServiceImpl implements OrganizationRequestService {

    private final OrganizationRequestRepository organizationRequestRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final OrganizationRequestMapper organizationRequestMapper;
    private final HelpNotificationService notificationService;

    public OrganizationRequestServiceImpl(
            OrganizationRequestRepository organizationRequestRepository,
            UserRepository userRepository,
            OrganizationRepository organizationRepository,
            OrganizationMemberRepository organizationMemberRepository,
            OrganizationRequestMapper organizationRequestMapper,
            HelpNotificationService notificationService) {
        this.organizationRequestRepository = organizationRequestRepository;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.organizationRequestMapper = organizationRequestMapper;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationRequest> getAllJoinInvitationRequests() {
        return organizationRequestRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationRequestDTO> getAllJoinRequestsByOrganizationId(UUID organizationId) {
        return organizationRequestRepository
                .findByOrganizationOrganizationIdAndRequestType(organizationId, OrganizationRequest.OrganizationRequestType.Join)
                .stream()
                .map(organizationRequestMapper::toDTO)
                .collect(Collectors.toList());
    }
    @Override
    @Transactional(readOnly = true)
    public List<OrganizationRequest> getAllJoinRequestsByUserId(UUID userId) {
        return organizationRequestRepository.findByUserIdAndRequestType(userId, OrganizationRequest.OrganizationRequestType.Join);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrganizationRequest> getJoinRequestById(UUID id) {
        return organizationRequestRepository.findById(id);
    }

    @Override
    @Transactional
    public OrganizationRequest createJoinRequest(OrganizationRequestDto organizationRequestDto) {
        OrganizationRequest newJoinRequest = new OrganizationRequest();
        User user = userRepository.findById(organizationRequestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + organizationRequestDto.getUserId()));
        Organization organization = organizationRepository.findById(organizationRequestDto.getOrganizationId()).orElseThrow(() -> new IllegalArgumentException("Organization not found with ID: " + organizationRequestDto.getOrganizationId()));

        if (organizationRequestRepository.findByUserIdAndOrganizationOrganizationId(organizationRequestDto.getUserId(), organizationRequestDto.getOrganizationId()) != null)
            throw new IllegalArgumentException("User already exists with ID: " + organizationRequestDto.getUserId());

        newJoinRequest.setRequestType(OrganizationRequest.OrganizationRequestType.Join);
        newJoinRequest.setStatus(OrganizationRequest.OrganizationRequestStatus.Pending);
        newJoinRequest.setOrganization(organizationRepository.findById(organizationRequestDto.getOrganizationId()).orElseThrow(() -> new IllegalArgumentException("Organization not found with ID: " + organizationRequestDto.getOrganizationId())));

        newJoinRequest.setUser(user);
        notificationService.notifyUser(
                organization.getCeo(),
                "New Join Request",
                null,
                "User " + user.getFullName() + " has sent a request to join the organization \"" + organization.getOrganizationName() + "\".",
                "/my-organization/members"
        );
        return organizationRequestRepository.save(newJoinRequest);
    }

    @Override
    @Transactional
    public OrganizationRequest acceptJoinRequest(UUID joinRequestId) {
        OrganizationRequest organizationRequest = organizationRequestRepository.findById(joinRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Join request not found with ID: " + joinRequestId));

        User requestUser = userRepository.findById(organizationRequest.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + organizationRequest.getUser().getId()));

        Organization organization = organizationRepository
                .findById(organizationRequest.getOrganization().getOrganizationId())
                .orElseThrow(() -> new IllegalArgumentException("Organization not found with ID: " + organizationRequest.getOrganization().getOrganizationId()));

        OrganizationMember newMember = new OrganizationMember();
        newMember.setUser(requestUser);
        newMember.setOrganization(organization);
        newMember.setMemberRole(OrganizationMember.OrganizationMemberRole.MEMBER);
        organizationMemberRepository.save(newMember);

        organizationRequest.setStatus(OrganizationRequest.OrganizationRequestStatus.Approved);
        notificationService.notifyUser(
                requestUser,
                "Join Request Approved",
                null,
                "Your request to join the organization \"" + organization.getOrganizationName() + "\" has been approved.",
                "/my-organization/members"
        );
        return organizationRequestRepository.save(organizationRequest);
    }

    @Override
    @Transactional
    public OrganizationRequest rejectJoinRequest(UUID joinRequestId) {
        OrganizationRequest organizationRequest = organizationRequestRepository
                .findById(joinRequestId).orElseThrow(() -> new IllegalArgumentException("Join request not found with ID: " + joinRequestId));

        organizationRequest.setStatus(OrganizationRequest.OrganizationRequestStatus.Rejected);
        notificationService.notifyUser(
                organizationRequest.getUser(),
                "Join Request Rejected",
                null,
                "Your request to join the organization \"" + organizationRequest.getOrganization().getOrganizationName() + "\" has been rejected.",
                "/"
        );
        return organizationRequestRepository.save(organizationRequest);
    }

    @Override
    @Transactional
    public void cancelJoinRequest(UUID joinRequestId) {
        organizationRequestRepository.deleteById(joinRequestId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationRequestDTO> getAllInvitationRequestsByOrganizationId(UUID organizationId) {
        return organizationRequestRepository.findByOrganizationOrganizationIdAndRequestType(organizationId, OrganizationRequest.OrganizationRequestType.Invitation)
                .stream().map(organizationRequestMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationRequest> getAllInvitationRequestsByUserId(UUID userId) {
        return organizationRequestRepository.findByUserIdAndRequestType(userId, OrganizationRequest.OrganizationRequestType.Invitation);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrganizationRequest> getInvitationRequestById(UUID id) {
        return organizationRequestRepository.findById(id);
    }

    @Override
    @Transactional
    public OrganizationRequestDTO createInvitationRequest(UUID organizationId, UUID userId) {
        OrganizationRequest organizationRequest = new OrganizationRequest();
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        Organization organization = organizationRepository.findById(organizationId).orElseThrow(() -> new IllegalArgumentException("Organization not found with ID: " + organizationId));


        if (organizationRequestRepository.findByUserIdAndOrganizationOrganizationId(user.getId(), organization.getOrganizationId()) != null) {
            System.out.println("OrganizationRequest already exists with ID: " + userId);
            throw new IllegalArgumentException("Invite request already exists");
        }

        organizationRequest.setUser(user);
        organizationRequest.setOrganization(organizationRepository.findById(organizationId).orElseThrow(() -> new IllegalArgumentException("Organization not found with ID: " + organizationId)));
        organizationRequest.setRequestType(fptu.fcharity.entity.OrganizationRequest.OrganizationRequestType.Invitation);
        organizationRequest.setStatus(fptu.fcharity.entity.OrganizationRequest.OrganizationRequestStatus.Pending);
        organizationRequestRepository.save(organizationRequest);

        System.out.println("Invite request created: 🤖🤖🤖🤖" + organizationRequest);

        notificationService.notifyUser(
                user,
                "Invitation to Join Organization",
                null,
                "You have been invited to join the organization \"" + organization.getOrganizationName() + "\".",
                "/user/manage-profile/invitations"
        );
        return organizationRequestMapper.toDTO(organizationRequestRepository.findByUserIdAndOrganizationOrganizationIdAndRequestType(userId, organizationId, fptu.fcharity.entity.OrganizationRequest.OrganizationRequestType.Invitation));
    }

    @Override
    @Transactional
    public OrganizationRequest acceptInvitationRequest(UUID invitationRequestId) {
        OrganizationRequest organizationRequest = organizationRequestRepository.findById(invitationRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Join request not found with ID: " + invitationRequestId));

        User requestUser = userRepository.findById(organizationRequest.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + organizationRequest.getUser().getId()));

        Organization organization = organizationRepository
                .findById(organizationRequest.getOrganization().getOrganizationId())
                .orElseThrow(() -> new IllegalArgumentException("Organization not found with ID: " + organizationRequest.getOrganization().getOrganizationId()));

        OrganizationMember newMember = new OrganizationMember();
        newMember.setUser(requestUser);
        newMember.setOrganization(organization);
        newMember.setMemberRole(OrganizationMember.OrganizationMemberRole.MEMBER);
        organizationMemberRepository.save(newMember);

        organizationRequest.setStatus(OrganizationRequest.OrganizationRequestStatus.Approved);
        notificationService.notifyUser(
                organization.getCeo(),
                "User has responded to your invitation",
                null,
                "User \"" + requestUser.getFullName() + "\" has accepted your invitation to join the organization \"" + organization.getOrganizationName() + "\".",
                "/my-organization/members"
        );
        return organizationRequestRepository.save(organizationRequest);
    }

    @Override
    @Transactional
    public OrganizationRequest rejectInvitationRequest(UUID invitationRequestId) {
        OrganizationRequest organizationRequest = organizationRequestRepository
                .findById(invitationRequestId).orElseThrow(() -> new IllegalArgumentException("Join request not found with ID: " + invitationRequestId));

        organizationRequest.setStatus(OrganizationRequest.OrganizationRequestStatus.Rejected);
        notificationService.notifyUser(
                organizationRequest.getOrganization().getCeo(),
                "User has responded to your invitation",
                null,
                "User \"" + organizationRequest.getUser().getFullName() + "\" has rejected your invitation to join the organization \"" + organizationRequest.getOrganization().getOrganizationName() + "\".",
                "/my-organization/members"
        );
        return organizationRequestRepository.save(organizationRequest);
    }

    @Override
    @Transactional
    public void cancelInvitationRequest(UUID invitationRequestId) {
        organizationRequestRepository.deleteById(invitationRequestId);
    }
}