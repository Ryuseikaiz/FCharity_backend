package fptu.fcharity.service.request;

import fptu.fcharity.entity.HelpRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RequestService {
    List<HelpRequest> getAll();
    Optional<HelpRequest> getById(UUID id);
    HelpRequest save(HelpRequest request);
    HelpRequest update(HelpRequest request);
    void delete(UUID id);
}
