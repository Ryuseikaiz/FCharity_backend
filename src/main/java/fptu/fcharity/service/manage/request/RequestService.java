package fptu.fcharity.service.manage.request;

import fptu.fcharity.dto.request.RequestDto;
import fptu.fcharity.entity.*;
import fptu.fcharity.repository.*;
import fptu.fcharity.repository.manage.request.RequestRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.response.request.RequestFinalResponse;
import fptu.fcharity.service.ObjectAttachmentService;
import fptu.fcharity.service.TaggableService;
import fptu.fcharity.utils.constants.RequestStatus;
import fptu.fcharity.utils.constants.TaggableType;
import fptu.fcharity.utils.exception.ApiRequestException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TaggableService taggableService;
    private final ObjectAttachmentService objectAttachmentService;

    public RequestService(
                                                     RequestRepository requestRepository,
                                                     UserRepository userRepository,
                                                     CategoryRepository categoryRepository,
                                                    TaggableService taggableService,
                                                     ObjectAttachmentService objectAttachmentService) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.taggableService = taggableService;
        this.objectAttachmentService = objectAttachmentService;
    }

    public List<RequestFinalResponse> getAllRequests() {
        List<HelpRequest> helpRequestList =  requestRepository.findAllWithInclude();
        return  helpRequestList.stream()
                .map(request -> new RequestFinalResponse(request,
                        taggableService.getTagsOfObject(request.getId(),TaggableType.REQUEST),
                        objectAttachmentService.getAttachmentsOfObject(request.getId(),TaggableType.REQUEST)
                ))
                .toList();
    }

    public RequestFinalResponse getRequestById(UUID requestId) {
        HelpRequest helpRequest =  requestRepository.findWithIncludeById(requestId);
        if(helpRequest == null){
            throw new ApiRequestException("Request not found");
        }
        return new RequestFinalResponse(helpRequest,
                taggableService.getTagsOfObject(helpRequest.getId(),TaggableType.REQUEST),
                objectAttachmentService.getAttachmentsOfObject(helpRequest.getId(),TaggableType.REQUEST)
        );
    }

    @Transactional
    public RequestFinalResponse createRequest(RequestDto requestDTO) {
       try{
           User user = userRepository.findById(requestDTO.getUserId())
                   .orElseThrow(() -> new ApiRequestException("User not found"));

            Category category = categoryRepository.findById(requestDTO.getCategoryId())
                    .orElseThrow(() -> new ApiRequestException("Category not found"));

           HelpRequest helpRequest = new HelpRequest(
                   user, requestDTO.getTitle(), requestDTO.getContent(),
                   requestDTO.getPhone(), requestDTO.getEmail(),
                   requestDTO.getLocation(),
                   requestDTO.isEmergency(), category);
           requestRepository.save(helpRequest);
           taggableService.addTaggables(helpRequest.getId(), requestDTO.getTagIds(),TaggableType.REQUEST);
           objectAttachmentService.saveAttachments(helpRequest.getId(), requestDTO.getImageUrls(), TaggableType.REQUEST);
           objectAttachmentService.saveAttachments(helpRequest.getId(), requestDTO.getVideoUrls(), TaggableType.REQUEST);

           return new RequestFinalResponse(helpRequest,
                   taggableService.getTagsOfObject(helpRequest.getId(),TaggableType.REQUEST),
                   objectAttachmentService.getAttachmentsOfObject(helpRequest.getId(),TaggableType.REQUEST));
       }catch(Exception e){
           throw new ApiRequestException(e.getMessage());
       }

    }

    public RequestFinalResponse updateRequest(UUID requestId, RequestDto requestDTO) {
        HelpRequest helpRequest = requestRepository.findWithIncludeById(requestId);
        if (helpRequest != null) {
            if (requestDTO.getCategoryId() != null) {
                Category category = categoryRepository.findById(requestDTO.getCategoryId()).get();
                helpRequest.setCategory(category);
            } else {
                helpRequest.setCategory(null);
            }
            helpRequest.setTitle(requestDTO.getTitle() != null ? requestDTO.getTitle() : helpRequest.getTitle());
            helpRequest.setContent(requestDTO.getContent() != null ? requestDTO.getContent() : helpRequest.getContent());
            helpRequest.setPhone(requestDTO.getPhone() != null ? requestDTO.getPhone() : helpRequest.getPhone());
            helpRequest.setEmail(requestDTO.getEmail() != null ? requestDTO.getEmail() : helpRequest.getEmail());
            helpRequest.setLocation(requestDTO.getLocation() != null ? requestDTO.getLocation() : helpRequest.getLocation());
            helpRequest.setIsEmergency(requestDTO.isEmergency());
            helpRequest.setStatus(requestDTO.getStatus() != null ? requestDTO.getStatus() : helpRequest.getStatus());
            if (requestDTO.getTagIds() != null) {
                taggableService.updateTaggables(helpRequest.getId(), requestDTO.getTagIds(),TaggableType.REQUEST);
            } else {
                taggableService.updateTaggables(helpRequest.getId(), new ArrayList<>(),TaggableType.REQUEST);
            }
            objectAttachmentService.clearAttachments(helpRequest.getId(), TaggableType.REQUEST);
            if(requestDTO.getImageUrls() != null){
                objectAttachmentService.saveAttachments(helpRequest.getId(), requestDTO.getImageUrls(), TaggableType.REQUEST);
            }
            if(requestDTO.getVideoUrls() != null){
                objectAttachmentService.saveAttachments(helpRequest.getId(), requestDTO.getVideoUrls(), TaggableType.REQUEST);
            }
            requestRepository.save(helpRequest);
            return new RequestFinalResponse(helpRequest,
                    taggableService.getTagsOfObject(helpRequest.getId(),TaggableType.REQUEST),
                    objectAttachmentService.getAttachmentsOfObject(helpRequest.getId(), TaggableType.REQUEST));
        }
        throw new ApiRequestException("Request not found");
    }

    public void deleteRequest(UUID requestId) {
        if (!requestRepository.existsById(requestId)) {
            throw new ApiRequestException("Request not found");
        }
        objectAttachmentService.clearAttachments(requestId, TaggableType.REQUEST);
        requestRepository.deleteById(requestId);
    }

    public List<RequestFinalResponse> getActiveRequests() {
        List<HelpRequest> helpRequestList =  requestRepository.findAllWithInclude();
        return  helpRequestList.stream()
                .filter(request -> request.getStatus().equals(RequestStatus.APPROVED))
                .map(request -> new RequestFinalResponse(request,
                        taggableService.getTagsOfObject(request.getId(),TaggableType.REQUEST),
                        objectAttachmentService.getAttachmentsOfObject(request.getId(),TaggableType.REQUEST)
                ))
                .toList();
    }

    public List<RequestFinalResponse> getRequestsByUserId(UUID userId) {
        List<HelpRequest> helpRequests = requestRepository.findByUserId(userId);
        return helpRequests.stream()
                .map(request -> new RequestFinalResponse(
                        request,
                        taggableService.getTagsOfObject(request.getId(), TaggableType.REQUEST),
                        objectAttachmentService.getAttachmentsOfObject(request.getId(), TaggableType.REQUEST)
                ))
                .toList();
    }
}