package fptu.fcharity.service.request;

import fptu.fcharity.entity.Request;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RequestService {
    List<Request> getAll();
    Optional<Request> getById(UUID id);
    Request save(Request request);
    Request update(Request request);
    void delete(UUID id);
}
