package fptu.fcharity.service.request;

import fptu.fcharity.dto.request.InviteJoinRequestDto;
import fptu.fcharity.entity.InviteJoinRequest;
import fptu.fcharity.entity.User;
import fptu.fcharity.repository.InviteJoinRequestRepository;
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

    public InviteJoinRequestServiceImpl(InviteJoinRequestRepository inviteJoinRequestRepository, UserRepository userRepository) {
        this.inviteJoinRequestRepository = inviteJoinRequestRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public InviteJoinRequest createJoinRequest(InviteJoinRequestDto inviteJoinRequestDto) {
        InviteJoinRequest inviteJoinRequest = new InviteJoinRequest();

        inviteJoinRequest.setTitle(inviteJoinRequestDto.getTitle());
        inviteJoinRequest.setContent(inviteJoinRequestDto.getContent());
        inviteJoinRequest.setCvLocation(inviteJoinRequestDto.getCvLocation());
        inviteJoinRequest.setRequestType("Request");
        inviteJoinRequest.setStatus(inviteJoinRequestDto.getStatus());
        inviteJoinRequest.setOrganizationId(inviteJoinRequestDto.getOrganizationId());
//        request.setCreatedAt(new Date());

        User user = userRepository.findById(inviteJoinRequestDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + inviteJoinRequestDto.getUserId()));
        inviteJoinRequest.setUser(user);
        return inviteJoinRequestRepository.save(inviteJoinRequest);
    }

    @Override
    @Transactional
    public InviteJoinRequest updateJoinRequest(InviteJoinRequest inviteJoinRequest) {
        System.out.println("updateJoinRequest");
        return inviteJoinRequestRepository.save(inviteJoinRequest);
    }

    @Override
    @Transactional
    public void deleteJoinRequest(InviteJoinRequest joinRequest) {
        inviteJoinRequestRepository.delete(joinRequest);
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
        return inviteJoinRequestRepository.findByOrganizationIdAndRequestType(organizationId, "Invite");
    }
}
