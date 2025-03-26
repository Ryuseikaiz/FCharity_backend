package fptu.fcharity.service.manage.request;

import fptu.fcharity.dto.request.RequestDto;
import fptu.fcharity.entity.*;
import fptu.fcharity.repository.*;
import fptu.fcharity.repository.manage.request.RequestRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.response.request.RequestFinalResponse;
import fptu.fcharity.service.ObjectAttachmentService;
import fptu.fcharity.service.TaggableService;
import fptu.fcharity.utils.constants.TaggableType;
import fptu.fcharity.utils.exception.ApiRequestException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
        List<HelpRequest> requestList =  requestRepository.findAllWithInclude();
        return  requestList.stream()
                .map(request -> new RequestFinalResponse(request,
                        taggableService.getTagsOfObject(request.getId(),TaggableType.REQUEST),
                        objectAttachmentService.getAttachmentsOfObject(request.getId(),TaggableType.REQUEST)
                ))
                .toList();
    }

    public RequestFinalResponse getRequestById(UUID requestId) {
        HelpRequest request =  requestRepository.findWithIncludeById(requestId);
        if(request == null){
            throw new ApiRequestException("Request not found");
        }
        return new RequestFinalResponse(request,
                taggableService.getTagsOfObject(request.getId(),TaggableType.REQUEST),
                objectAttachmentService.getAttachmentsOfObject(request.getId(),TaggableType.REQUEST)
        );
    }

    @Transactional
    public RequestFinalResponse createRequest(RequestDto requestDTO) {
       try{
           User user = userRepository.findById(requestDTO.getUserId())
                   .orElseThrow(() -> new ApiRequestException("User not found"));

            Category category = categoryRepository.findById(requestDTO.getCategoryId())
                    .orElseThrow(() -> new ApiRequestException("Category not found"));

           HelpRequest request = new HelpRequest(
                   user, requestDTO.getTitle(), requestDTO.getContent(),
                   requestDTO.getPhone(), requestDTO.getEmail(),
                   requestDTO.getFullAddress(),
                   requestDTO.isEmergency(), category);
           requestRepository.save(request);
           taggableService.addTaggables(request.getId(), requestDTO.getTagIds(),TaggableType.REQUEST);
           objectAttachmentService.saveAttachments(request.getId(), requestDTO.getImageUrls(), TaggableType.REQUEST);
           objectAttachmentService.saveAttachments(request.getId(), requestDTO.getVideoUrls(), TaggableType.REQUEST);

           return new RequestFinalResponse(request,
                   taggableService.getTagsOfObject(request.getId(),TaggableType.REQUEST),
                   objectAttachmentService.getAttachmentsOfObject(request.getId(),TaggableType.REQUEST));
       }catch(Exception e){
           throw new ApiRequestException(e.getMessage());
       }

    }

    public RequestFinalResponse updateRequest(UUID requestId, RequestDto requestDTO) {
        HelpRequest request = requestRepository.findWithIncludeById(requestId);
        if (request != null) {
            if (requestDTO.getCategoryId() != null) {
                Category category = categoryRepository.findById(requestDTO.getCategoryId()).get();
                request.setCategory(category);
            } else {
                request.setCategory(null);
            }
            request.setTitle(requestDTO.getTitle() != null ? requestDTO.getTitle() : request.getTitle());
            request.setContent(requestDTO.getContent() != null ? requestDTO.getContent() : request.getContent());
            request.setPhone(requestDTO.getPhone() != null ? requestDTO.getPhone() : request.getPhone());
            request.setEmail(requestDTO.getEmail() != null ? requestDTO.getEmail() : request.getEmail());
            request.setLocation(requestDTO.getFullAddress() != null ? requestDTO.getFullAddress() : request.getLocation());
            request.setIsEmergency(requestDTO.isEmergency());
            request.setStatus(requestDTO.getStatus() != null ? requestDTO.getStatus() : request.getStatus());
            if (requestDTO.getTagIds() != null) {
                taggableService.updateTaggables(request.getId(), requestDTO.getTagIds(),TaggableType.REQUEST);
            } else {
                taggableService.updateTaggables(request.getId(), new ArrayList<>(),TaggableType.REQUEST);
            }
            objectAttachmentService.clearAttachments(request.getId(), TaggableType.REQUEST);
            if(requestDTO.getImageUrls() != null){
                objectAttachmentService.saveAttachments(request.getId(), requestDTO.getImageUrls(), TaggableType.REQUEST);
            }
            if(requestDTO.getVideoUrls() != null){
                objectAttachmentService.saveAttachments(request.getId(), requestDTO.getVideoUrls(), TaggableType.REQUEST);
            }
            requestRepository.save(request);
            return new RequestFinalResponse(request,
                    taggableService.getTagsOfObject(request.getId(),TaggableType.REQUEST),
                    objectAttachmentService.getAttachmentsOfObject(request.getId(), TaggableType.REQUEST));
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
        List<HelpRequest> requestList =  requestRepository.findAllWithInclude();
        return  requestList.stream()
                .filter(request -> request.getStatus().equals("ACTIVE"))
                .map(request -> new RequestFinalResponse(request,
                        taggableService.getTagsOfObject(request.getId(),TaggableType.REQUEST),
                        objectAttachmentService.getAttachmentsOfObject(request.getId(),TaggableType.REQUEST)
                ))
                .toList();
    }

    public List<RequestFinalResponse> getRequestsByUserId(UUID userId) {
        List<HelpRequest> requests = requestRepository.findByUserId(userId);
        return requests.stream()
                .map(request -> new RequestFinalResponse(
                        request,
                        taggableService.getTagsOfObject(request.getId(), TaggableType.REQUEST),
                        objectAttachmentService.getAttachmentsOfObject(request.getId(), TaggableType.REQUEST)
                ))
                .toList();
    }
}