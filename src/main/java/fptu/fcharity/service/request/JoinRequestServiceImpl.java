package fptu.fcharity.service.request;

import fptu.fcharity.entity.JoinRequest;
import fptu.fcharity.repository.JoinRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JoinRequestServiceImpl implements JoinRequestService {
    private final JoinRequestRepository joinRequestRepository;

    public JoinRequestServiceImpl(JoinRequestRepository joinRequestRepository) {
        this.joinRequestRepository = joinRequestRepository;
    }

    @Override
    @Transactional
    public JoinRequest createJoinRequest(JoinRequest joinRequest) {
        return joinRequestRepository.save(joinRequest);
    }

    @Override
    @Transactional
    public JoinRequest updateJoinRequest(JoinRequest joinRequest) {
        return joinRequestRepository.save(joinRequest);
    }

    @Override
    @Transactional
    public void deleteJoinRequest(JoinRequest joinRequest) {
        joinRequestRepository.delete(joinRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JoinRequest> getAllJoinRequests() {
        return joinRequestRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<JoinRequest> getAllJoinRequestsByOrganizationId(UUID organizationId) {
        return joinRequestRepository.findByOrganizationId(organizationId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<JoinRequest> getJoinRequestById(UUID id) {
        return joinRequestRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JoinRequest> getAllJoinRequestsByUserId(UUID userId) {
        return joinRequestRepository.findByUserId(userId);
    }
}
