package fptu.fcharity.service.request;

import fptu.fcharity.dto.request.OrganizationRequestDto;
import fptu.fcharity.entity.Organization;
import fptu.fcharity.entity.OrganizationMember;
import fptu.fcharity.entity.OrganizationRequest;
import fptu.fcharity.entity.User;
import fptu.fcharity.repository.OrganizationRequestRepository;

import fptu.fcharity.repository.manage.organization.OrganizationMemberRepository;
import fptu.fcharity.repository.manage.organization.OrganizationRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizationRequestServiceImpl implements OrganizationRequestService {
    private final OrganizationRequestRepository OrganizationRequestRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    public OrganizationRequestServiceImpl(OrganizationRequestRepository OrganizationRequestRepository, UserRepository userRepository, OrganizationRepository organizationRepository, OrganizationMemberRepository organizationMemberRepository) {
        this.OrganizationRequestRepository = OrganizationRequestRepository;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.organizationMemberRepository = organizationMemberRepository;
    }

    @Override
    @Transactional
    public OrganizationRequest createJoinRequest(OrganizationRequestDto OrganizationRequestDto) {
        OrganizationRequest newJoinRequest = new OrganizationRequest();
        User user = userRepository.findById(OrganizationRequestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + OrganizationRequestDto.getUserId()));
        Organization organization = organizationRepository.findById(OrganizationRequestDto.getOrganizationId()).orElseThrow(() -> new IllegalArgumentException("Organization not found with ID: " + OrganizationRequestDto.getOrganizationId()));

        if (OrganizationRequestRepository.findByUserUserIdAndOrganizationOrganizationId(OrganizationRequestDto.getUserId(), OrganizationRequestDto.getOrganizationId()) != null)
            throw new IllegalArgumentException("User already exists with ID: " + OrganizationRequestDto.getUserId());

        newJoinRequest.setRequestType(OrganizationRequest.OrganizationRequestType.Request);
        newJoinRequest.setStatus(OrganizationRequest.OrganizationRequestStatus.Pending);
        newJoinRequest.setOrganization(organizationRepository.findById(OrganizationRequestDto.getOrganizationId()).orElseThrow(() -> new IllegalArgumentException("Organization not found with ID: " + OrganizationRequestDto.getOrganizationId())));

        newJoinRequest.setUser(user);
        return OrganizationRequestRepository.save(newJoinRequest);
    }

    @Override
    @Transactional
    public OrganizationRequest updateJoinRequest(OrganizationRequestDto OrganizationRequestDto) {
        User user = userRepository.findById(OrganizationRequestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + OrganizationRequestDto.getUserId()));
        Organization organization = organizationRepository.findById(OrganizationRequestDto.getOrganizationId()).orElseThrow(() -> new IllegalArgumentException("Organization not found with ID: " + OrganizationRequestDto.getOrganizationId()));

        OrganizationRequest OrganizationRequest = OrganizationRequestRepository.findByUserUserIdAndOrganizationOrganizationId(OrganizationRequestDto.getUserId(), OrganizationRequestDto.getOrganizationId());

        if ( OrganizationRequest == null)
            throw new IllegalArgumentException("OrganizationRequest not found with ID: " + OrganizationRequestDto.getUserId());


        switch (OrganizationRequestDto.getStatus()) {
            case fptu.fcharity.entity.OrganizationRequest.OrganizationRequestStatus.Approved:
                System.out.println("created new organization member: ");

                OrganizationMember newMember = new OrganizationMember();
                newMember.setUser(user);
                newMember.setOrganization(organization);

                System.out.println(organizationMemberRepository.save(newMember));
                break;
            case fptu.fcharity.entity.OrganizationRequest.OrganizationRequestStatus.Rejected:
                break;
            default:
                break;
        }

        OrganizationRequest.setRequestType(fptu.fcharity.entity.OrganizationRequest.OrganizationRequestType.Request);
        OrganizationRequest.setStatus(OrganizationRequestDto.getStatus());
        OrganizationRequest.setOrganization(organizationRepository.findById(OrganizationRequestDto.getOrganizationId()).orElseThrow(() -> new IllegalArgumentException("Organization not found with ID: " + OrganizationRequestDto.getOrganizationId())));

        OrganizationRequest.setUser(user);
        return OrganizationRequestRepository.save(OrganizationRequest);
    }

    @Override
    @Transactional
    public void deleteJoinRequest(UUID joinRequestId) {
        if (!OrganizationRequestRepository.existsById(joinRequestId))
            throw new IllegalArgumentException("OrganizationRequest not found with ID: " + joinRequestId);
        OrganizationRequestRepository.deleteById(joinRequestId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationRequest> getAllJoinRequests() {
        return OrganizationRequestRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationRequest> getAllJoinRequestsByOrganizationId(UUID organizationId) {
        return OrganizationRequestRepository.findByOrganizationOrganizationIdAndRequestType(organizationId, OrganizationRequest.OrganizationRequestType.Request);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrganizationRequest> getJoinRequestById(UUID id) {
        return OrganizationRequestRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationRequest> getAllJoinRequestsByUserId(UUID userId) {
        return OrganizationRequestRepository.findByUserUserIdAndRequestType(userId, OrganizationRequest.OrganizationRequestType.Request);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrganizationRequest> getInviteRequestById(UUID id) {
        return OrganizationRequestRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationRequest> getAllInviteRequestsByOrganizationId(UUID organizationId) {
        return OrganizationRequestRepository.findByOrganizationOrganizationIdAndRequestType(organizationId, OrganizationRequest.OrganizationRequestType.Invitation);
    }

    @Override
    public OrganizationRequest createInviteRequest(OrganizationRequestDto OrganizationRequestDto) {
        OrganizationRequest OrganizationRequest = new OrganizationRequest();
        User user = userRepository.findById(OrganizationRequestDto.getUserId()).orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + OrganizationRequestDto.getUserId()));
        System.out.println("creating new invite request: ");

        Organization organization = organizationRepository.findById(OrganizationRequestDto.getOrganizationId()).orElseThrow(() -> new IllegalArgumentException("Organization not found with ID: " + OrganizationRequestDto.getOrganizationId()));


       if (OrganizationRequestRepository.findByUserUserIdAndOrganizationOrganizationId(user.getUserId(), OrganizationRequest.getOrganization().getOrganizationId()) != null) {
           System.out.println("OrganizationRequest already exists with ID: " + OrganizationRequestDto.getUserId());
           throw new IllegalArgumentException("Invite request already exists");
       }

       OrganizationRequest.setUser(user);
       OrganizationRequest.setOrganization(organizationRepository.findById(OrganizationRequestDto.getOrganizationId()).orElseThrow(() -> new IllegalArgumentException("Organization not found with ID: " + OrganizationRequestDto.getOrganizationId())));
       OrganizationRequest.setRequestType(fptu.fcharity.entity.OrganizationRequest.OrganizationRequestType.Invitation);
       OrganizationRequest.setStatus(fptu.fcharity.entity.OrganizationRequest.OrganizationRequestStatus.Pending);
       return OrganizationRequestRepository.save(OrganizationRequest);
    }

    @Override
    public OrganizationRequest updateInviteRequest(OrganizationRequestDto OrganizationRequestDto) {
        User user = userRepository.findById(OrganizationRequestDto.getUserId()).orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + OrganizationRequestDto.getUserId()));
        Organization organization = organizationRepository.findById(OrganizationRequestDto.getOrganizationId()).orElseThrow(() -> new IllegalArgumentException("Organization not found with ID: " + OrganizationRequestDto.getOrganizationId()));

        OrganizationRequest OrganizationRequest = OrganizationRequestRepository.findByUserUserIdAndOrganizationOrganizationId(user.getUserId(), organization.getOrganizationId());
        if (OrganizationRequest == null)
            throw new IllegalArgumentException("Invite request does not exist");

        switch (OrganizationRequestDto.getStatus()) {
            case fptu.fcharity.entity.OrganizationRequest.OrganizationRequestStatus.Approved:
                System.out.println("created new organization member: ");

                OrganizationMember newMember = new OrganizationMember();
                newMember.setUser(user);
                newMember.setOrganization(organization);

                System.out.println(organizationMemberRepository.save(newMember));
                break;
            case fptu.fcharity.entity.OrganizationRequest.OrganizationRequestStatus.Rejected:
                break;
            default:
                break;
        }

        OrganizationRequest.setUser(user);
        OrganizationRequest.setOrganization(organizationRepository.findById(OrganizationRequestDto.getOrganizationId()).orElseThrow(() -> new IllegalArgumentException("Organization not found with ID: " + OrganizationRequestDto.getOrganizationId())));
        OrganizationRequest.setRequestType(fptu.fcharity.entity.OrganizationRequest.OrganizationRequestType.Invitation);
        OrganizationRequest.setStatus(OrganizationRequestDto.getStatus());

        return OrganizationRequestRepository.save(OrganizationRequest);
    }

    @Override
    public void deleteInviteRequest(UUID id) {
        if (!OrganizationRequestRepository.existsById(id))
            throw new IllegalArgumentException("Invite request does not exist");
        OrganizationRequestRepository.deleteById(id);
    }
}
