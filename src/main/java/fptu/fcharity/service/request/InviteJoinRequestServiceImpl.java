package fptu.fcharity.service.request;

import fptu.fcharity.dto.request.InviteJoinRequestDto;
import fptu.fcharity.entity.InviteJoinRequest;
import fptu.fcharity.entity.Organization;
import fptu.fcharity.entity.OrganizationMember;
import fptu.fcharity.entity.User;
import fptu.fcharity.repository.InviteJoinRequestRepository;
import fptu.fcharity.repository.OrganizationMemberRepository;
import fptu.fcharity.repository.OrganizationRepository;
import fptu.fcharity.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InviteJoinRequestServiceImpl implements InviteJoinRequestService {
    private final InviteJoinRequestRepository inviteJoinRequestRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    public InviteJoinRequestServiceImpl(InviteJoinRequestRepository inviteJoinRequestRepository, UserRepository userRepository, OrganizationRepository organizationRepository, OrganizationMemberRepository organizationMemberRepository) {
        this.inviteJoinRequestRepository = inviteJoinRequestRepository;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.organizationMemberRepository = organizationMemberRepository;
    }

    @Override
    @Transactional
    public InviteJoinRequest createJoinRequest(InviteJoinRequestDto inviteJoinRequestDto) {
        InviteJoinRequest inviteJoinRequest = new InviteJoinRequest();
        User user = userRepository.findById(inviteJoinRequestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + inviteJoinRequestDto.getUserId()));
        Organization organization = organizationRepository.findById(inviteJoinRequestDto.getOrganizationId()).orElseThrow(() -> new IllegalArgumentException("Organization not found with ID: " + inviteJoinRequestDto.getOrganizationId()));

        if (inviteJoinRequestRepository.findByUserUserIdAndOrganizationId(inviteJoinRequestDto.getUserId(), inviteJoinRequestDto.getOrganizationId()) != null)
            throw new IllegalArgumentException("User already exists with ID: " + inviteJoinRequestDto.getUserId());

        inviteJoinRequest.setTitle(inviteJoinRequestDto.getTitle());
        inviteJoinRequest.setContent(inviteJoinRequestDto.getContent());
        inviteJoinRequest.setCvLocation(inviteJoinRequestDto.getCvLocation());
        inviteJoinRequest.setRequestType("Request");
        inviteJoinRequest.setStatus("Pending");
        inviteJoinRequest.setOrganizationId(inviteJoinRequestDto.getOrganizationId());

        inviteJoinRequest.setUser(user);
        return inviteJoinRequestRepository.save(inviteJoinRequest);
    }

    @Override
    @Transactional
    public InviteJoinRequest updateJoinRequest(InviteJoinRequestDto inviteJoinRequestDto) {
        InviteJoinRequest inviteJoinRequest = new InviteJoinRequest();
        User user = userRepository.findById(inviteJoinRequestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + inviteJoinRequestDto.getUserId()));
        Organization organization = organizationRepository.findById(inviteJoinRequestDto.getOrganizationId()).orElseThrow(() -> new IllegalArgumentException("Organization not found with ID: " + inviteJoinRequestDto.getOrganizationId()));

        if (inviteJoinRequestRepository.findByUserUserIdAndOrganizationId(inviteJoinRequestDto.getUserId(), inviteJoinRequestDto.getOrganizationId()) == null)
            throw new IllegalArgumentException("InviteJoinRequest not found with ID: " + inviteJoinRequestDto.getUserId());

        if (inviteJoinRequestRepository.findByInviteJoinRequestId(inviteJoinRequestDto.getInviteJoinRequestId()) != null) {
            switch (inviteJoinRequest.getStatus()) {
                case "Approved":
                    System.out.println("created new organization member: ");

                    OrganizationMember newMember = new OrganizationMember();
                    newMember.setUser(user);
                    newMember.setOrganization(organization);

                    System.out.println(organizationMemberRepository.save(newMember));
                    break;
                case "Rejected":
                    break;
                default:
                    break;
            }
        } else throw new IllegalArgumentException("InviteJoinRequest not found with ID: " + inviteJoinRequestDto.getUserId());

        inviteJoinRequest.setTitle(inviteJoinRequestDto.getTitle());
        inviteJoinRequest.setContent(inviteJoinRequestDto.getContent());
        inviteJoinRequest.setCvLocation(inviteJoinRequestDto.getCvLocation());
        inviteJoinRequest.setRequestType("Request");
        inviteJoinRequest.setStatus(inviteJoinRequestDto.getStatus());
        inviteJoinRequest.setOrganizationId(inviteJoinRequestDto.getOrganizationId());

        inviteJoinRequest.setUser(user);
        return inviteJoinRequestRepository.save(inviteJoinRequest);
    }

    @Override
    @Transactional
    public void deleteJoinRequest(UUID joinRequestId) {
        if (!inviteJoinRequestRepository.existsById(joinRequestId))
            throw new IllegalArgumentException("InviteJoinRequest not found with ID: " + joinRequestId);
        inviteJoinRequestRepository.deleteById(joinRequestId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InviteJoinRequest> getAllJoinRequests() {
        return inviteJoinRequestRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InviteJoinRequest> getAllJoinRequestsByOrganizationId(UUID organizationId) {
        return inviteJoinRequestRepository.findByOrganizationIdAndRequestType(organizationId, "Request");
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InviteJoinRequest> getJoinRequestById(UUID id) {
        return inviteJoinRequestRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InviteJoinRequest> getAllJoinRequestsByUserId(UUID userId) {
        return inviteJoinRequestRepository.findByUserUserIdAndRequestType(userId, "Request");
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<InviteJoinRequest> getInviteRequestById(UUID id) {
        return inviteJoinRequestRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InviteJoinRequest> getAllInviteRequestsByOrganizationId(UUID organizationId) {
        return inviteJoinRequestRepository.findByOrganizationIdAndRequestType(organizationId, "Invitation");
    }

    @Override
    public InviteJoinRequest createInviteRequest(InviteJoinRequestDto inviteJoinRequestDto) {
        InviteJoinRequest inviteJoinRequest = new InviteJoinRequest();
        User user = userRepository.findById(inviteJoinRequestDto.getUserId()).orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + inviteJoinRequestDto.getUserId()));
        System.out.println("creating new invite request: ");

        Organization organization = organizationRepository.findById(inviteJoinRequestDto.getOrganizationId()).orElseThrow(() -> new IllegalArgumentException("Organization not found with ID: " + inviteJoinRequestDto.getOrganizationId()));


       if (inviteJoinRequestRepository.findByUserUserIdAndOrganizationId(user.getUserId(), inviteJoinRequest.getOrganizationId()) != null) {
           System.out.println("InviteJoinRequest already exists with ID: " + inviteJoinRequestDto.getUserId());
           throw new IllegalArgumentException("Invite request already exists");
       }

       inviteJoinRequest.setUser(user);
       inviteJoinRequest.setOrganizationId(inviteJoinRequestDto.getOrganizationId());
       inviteJoinRequest.setRequestType("Invitation");
       inviteJoinRequest.setStatus("Pending");
       return inviteJoinRequestRepository.save(inviteJoinRequest);
    }

    @Override
    public InviteJoinRequest updateInviteRequest(InviteJoinRequestDto inviteJoinRequestDto) {
        InviteJoinRequest inviteJoinRequest = new InviteJoinRequest();
        User user = userRepository.findById(inviteJoinRequestDto.getUserId()).orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + inviteJoinRequestDto.getUserId()));
        Organization organization = organizationRepository.findById(inviteJoinRequestDto.getOrganizationId()).orElseThrow(() -> new IllegalArgumentException("Organization not found with ID: " + inviteJoinRequestDto.getOrganizationId()));

        if (inviteJoinRequestRepository.findByUserUserIdAndOrganizationId(user.getUserId(), inviteJoinRequest.getOrganizationId()) == null)
            throw new IllegalArgumentException("Invite request does not exist");

        if (inviteJoinRequestRepository.findByInviteJoinRequestId(inviteJoinRequestDto.getInviteJoinRequestId()) != null) {
            switch (inviteJoinRequest.getStatus()) {
                case "Approved":
                    System.out.println("created new organization member: ");

                    OrganizationMember newMember = new OrganizationMember();
                    newMember.setUser(user);
                    newMember.setOrganization(organization);

                    System.out.println(organizationMemberRepository.save(newMember));
                    break;
                case "Rejected":
                    break;
                default:
                    break;
            }
        } else
            throw new IllegalArgumentException("Invite request does not exist");

        inviteJoinRequest.setUser(user);
        inviteJoinRequest.setOrganizationId(inviteJoinRequestDto.getOrganizationId());
        inviteJoinRequest.setRequestType("Invitation");
        inviteJoinRequest.setStatus(inviteJoinRequestDto.getStatus());

        return inviteJoinRequestRepository.save(inviteJoinRequest);
    }

    @Override
    public void deleteInviteRequest(UUID id) {
        if (!inviteJoinRequestRepository.existsById(id))
            throw new IllegalArgumentException("Invite request does not exist");
        inviteJoinRequestRepository.deleteById(id);
    }
}
