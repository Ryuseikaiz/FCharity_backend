package fptu.fcharity.service;

import fptu.fcharity.entity.Request;
import fptu.fcharity.repository.RequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RequestService {
    private final RequestRepository requestRepository;

    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    public Request getRequestById(UUID requestId) {
        return requestRepository.findById(requestId).orElse(null);
    }

    public Request createRequest(Request request) {
        request.setRequestId(UUID.randomUUID());
        return requestRepository.save(request);
    }

    public Request updateRequest(UUID requestId, Request requestDetails) {
        Request request = requestRepository.findById(requestId).orElse(null);
        if (request != null) {
            request.setTitle(requestDetails.getTitle());
            request.setContent(requestDetails.getContent());
            request.setPhone(requestDetails.getPhone());
            request.setEmail(requestDetails.getEmail());
            request.setLocation(requestDetails.getLocation());
            request.setAttachment(requestDetails.getAttachment());
            request.setEmergency(requestDetails.isEmergency());
            request.setCategory(requestDetails.getCategory());
            request.setTag(requestDetails.getTag());
            request.setStatus(requestDetails.getStatus());
            return requestRepository.save(request);
        }
        return null;
    }

    public void deleteRequest(UUID requestId) {
        requestRepository.deleteById(requestId);
    }
}