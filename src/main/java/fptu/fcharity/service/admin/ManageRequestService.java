package fptu.fcharity.service.admin;

import fptu.fcharity.dto.request.RequestDto;
import fptu.fcharity.dto.admindashboard.ReasonDTO;
import fptu.fcharity.entity.HelpRequest;
import fptu.fcharity.repository.manage.request.RequestRepository;
import fptu.fcharity.service.HelpNotificationService;
import fptu.fcharity.utils.constants.request.RequestStatus;
import fptu.fcharity.utils.exception.ApiRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManageRequestService {
    private final RequestRepository requestRepository;
    private final HelpNotificationService notificationService;

    // public List<RequestDto> getAllRequests() {
    // return requestRepository.findAll().stream()
    // .map(this::convertToDTO)
    // .collect(Collectors.toList());
    // }
    public List<RequestDto> getAllRequests() {
        Pageable pageable = PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "creationDate"));
        return requestRepository.findAll(pageable).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public RequestDto getRequestById(UUID requestId) {
        HelpRequest helpRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new ApiRequestException("Request not found with ID: " + requestId));
        return convertToDTO(helpRequest);
    }

    @Transactional
    public void deleteRequest(UUID requestId) {
        HelpRequest helpRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new ApiRequestException("Request not found with ID: " + requestId));
        requestRepository.delete(helpRequest);
    }

    @Transactional
    public void approveRequest(UUID requestId) {
        HelpRequest helpRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new ApiRequestException("Request not found with ID: " + requestId));

        if (!RequestStatus.PENDING.equals(helpRequest.getStatus())) {
            throw new ApiRequestException("Only pending requests can be approve.");
        }

        helpRequest.setStatus(RequestStatus.APPROVED);
        requestRepository.save(helpRequest);
        notificationService.notifyUser(
                helpRequest.getUser(),
                "Request Approved",
                null,
                "Your request \"" + helpRequest.getTitle() + "\" has been approved.",
                "/user/request/" + requestId
        );
    }

    @Transactional
    public void rejectRequest(UUID requestId, ReasonDTO reasonDTO) {
        HelpRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ApiRequestException("Request not found with ID: " + requestId));

        if (!RequestStatus.PENDING.equals(request.getStatus())) {
            throw new ApiRequestException("Only pending requests can be reject.");
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setReason(reasonDTO.getReason());
        requestRepository.save(request);

        notificationService.notifyUser(
                request.getUser(),
                "Yeu cau bi tu choi",
                null,
                "Yeu Cau \"" + request.getTitle() + "\" da bi tu choi voi li do: " + reasonDTO.getReason(),
                "/user/request/" + requestId
        );
    }

    @Transactional
    public void hideRequest(UUID requestId) {
        HelpRequest helpRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new ApiRequestException("Request not found with ID: " + requestId));

        if (!RequestStatus.APPROVED.equals(helpRequest.getStatus())) {
            throw new ApiRequestException("Only approved requests can be hidden.");
        }

        helpRequest.setStatus(RequestStatus.HIDDEN);
        requestRepository.save(helpRequest);
    }

    private RequestDto convertToDTO(HelpRequest helpRequest) {
        RequestDto dto = new RequestDto();
        dto.setId(helpRequest.getId());
        dto.setUserId(helpRequest.getUser() != null ? helpRequest.getUser().getId() : null);
        dto.setTitle(helpRequest.getTitle());
        dto.setContent(helpRequest.getContent());
        dto.setCreationDate(helpRequest.getCreationDate());
        dto.setPhone(helpRequest.getPhone());
        dto.setEmail(helpRequest.getEmail());
        dto.setFullAddress(helpRequest.getLocation());
        dto.setEmergency(helpRequest.getIsEmergency() != null ? helpRequest.getIsEmergency() : false);
        dto.setCategoryId(helpRequest.getCategory() != null ? helpRequest.getCategory().getId() : null);
        dto.setStatus(helpRequest.getStatus());

        dto.setTagIds(null);
        dto.setImageUrls(null);
        dto.setVideoUrls(null);
        dto.setReason(helpRequest.getReason());
        return dto;
    }
}
