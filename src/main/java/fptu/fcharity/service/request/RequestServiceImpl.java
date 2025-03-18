package fptu.fcharity.service.request;

import fptu.fcharity.entity.Request;

import fptu.fcharity.repository.manage.request.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;

    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Request> getAll() {
        return requestRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Request> getById(UUID id) {
        return requestRepository.findById(id);
    }

    @Override
    @Transactional
    public Request save(Request request) {
        return requestRepository.save(request);
    }

    @Override
    @Transactional
    public Request update(Request request) {
        return requestRepository.save(request);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        requestRepository.deleteById(id);
    }
}
