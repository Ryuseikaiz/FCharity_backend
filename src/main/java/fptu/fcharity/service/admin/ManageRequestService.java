package fptu.fcharity.service.admin;


import fptu.fcharity.dto.request.RequestDto;
import fptu.fcharity.entity.Request;
import fptu.fcharity.repository.manage.request.RequestRepository;
import fptu.fcharity.utils.constants.RequestStatus;
import fptu.fcharity.utils.exception.ApiRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManageRequestService {
    private final RequestRepository requestRepository;

    public List<RequestDto> getAllRequests() {
        return requestRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public RequestDto getRequestById(UUID requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ApiRequestException("Request not found with ID: " + requestId));
        return convertToDTO(request);
    }

    @Transactional
    public void deleteRequest(UUID requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ApiRequestException("Request not found with ID: " + requestId));
        requestRepository.delete(request);
    }

//    @Transactional
//    public void hideRequest(UUID requestId) {
//        Request request = requestRepository.findById(requestId)
//                .orElseThrow(() -> new ApiRequestException("Request not found with ID: " + requestId));
//
//        if (!RequestStatus.APPROVED.equals(request.getStatus())) {
//            throw new ApiRequestException("Only approved requests can be hidden.");
//        }
//
//        request.setStatus(RequestStatus.HIDDEN);
//        requestRepository.save(request);
//    }

    @Transactional
    public void approveRequest(UUID requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ApiRequestException("Request not found with ID: " + requestId));

        if (!RequestStatus.PENDING.equals(request.getStatus())) {
            throw new ApiRequestException("Only pending requests can be approve.");
        }

        request.setStatus(RequestStatus.APPROVED);
        requestRepository.save(request);
    }

    @Transactional
    public void rejectRequest(UUID requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ApiRequestException("Request not found with ID: " + requestId));

        if (!RequestStatus.PENDING.equals(request.getStatus())) {
            throw new ApiRequestException("Only pending requests can be reject.");
        }

        request.setStatus(RequestStatus.REJECTED);
        requestRepository.save(request);
    }

    private RequestDto convertToDTO(Request request) {
        RequestDto dto = new RequestDto();
        dto.setId(request.getId());
        dto.setUserId(request.getUser() != null ? request.getUser().getId() : null);
        dto.setTitle(request.getTitle());
        dto.setContent(request.getContent());
        dto.setCreationDate(request.getCreationDate());
        dto.setPhone(request.getPhone());
        dto.setEmail(request.getEmail());
        dto.setLocation(request.getLocation());
        dto.setEmergency(request.getIsEmergency() != null ? request.getIsEmergency() : false);
        dto.setCategoryId(request.getCategory() != null ? request.getCategory().getId() : null);
        dto.setStatus(request.getStatus());

        dto.setTagIds(null);
        dto.setImageUrls(null);
        dto.setVideoUrls(null);

        return dto;
    }
}
