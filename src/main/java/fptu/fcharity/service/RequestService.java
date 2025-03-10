package fptu.fcharity.service;

import fptu.fcharity.dto.request.RequestDto;
import fptu.fcharity.entity.*;
import fptu.fcharity.repository.*;
import fptu.fcharity.response.request.RequestFinalResponse;
import fptu.fcharity.utils.constants.TaggableType;
import fptu.fcharity.utils.exception.ApiRequestException;
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

    public RequestService(TaggableRepository _taggableRepository,
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
        List<Request> requestList =  requestRepository.findAllWithInclude();
        return  requestList.stream()
                .map(request -> new RequestFinalResponse(request,
                        taggableService.getTagsOfObject(request.getId(),TaggableType.REQUEST),
                        objectAttachmentService.getAttachmentsOfObject(request.getId(),TaggableType.REQUEST)
                ))
                .toList();
    }

    public RequestFinalResponse getRequestById(UUID requestId) {
        Request request =  requestRepository.findWithIncludeById(requestId);
        return new RequestFinalResponse(request,
                taggableService.getTagsOfObject(request.getId(),TaggableType.REQUEST),
                objectAttachmentService.getAttachmentsOfObject(request.getId(),TaggableType.REQUEST)
        );
    }

    public RequestFinalResponse createRequest(RequestDto requestDTO) {
       try{
           User user = userRepository.findById(requestDTO.getUserId())
                   .orElseThrow(() -> new ApiRequestException("User not found"));

            Category category = categoryRepository.findById(requestDTO.getCategoryId())
                    .orElseThrow(() -> new ApiRequestException("Category not found"));

           Request request = new Request(UUID.randomUUID(),
                   user, requestDTO.getTitle(), requestDTO.getContent(),
                   requestDTO.getPhone(), requestDTO.getEmail(),
                   requestDTO.getLocation(),
                   requestDTO.isEmergency(), category);
           requestRepository.save(request);
           taggableService.addTaggables(request.getId(), requestDTO.getTagIds(),TaggableType.REQUEST);
           objectAttachmentService.saveAttachments(request.getId(), requestDTO.getImageUrls(), "REQUEST");
           objectAttachmentService.saveAttachments(request.getId(), requestDTO.getVideoUrls(), "REQUEST");

           return new RequestFinalResponse(request,
                   taggableService.getTagsOfObject(request.getId(),TaggableType.REQUEST),
                   objectAttachmentService.getAttachmentsOfObject(request.getId(),TaggableType.REQUEST));
       }catch(Exception e){
           throw new ApiRequestException(e.getMessage());
       }

    }

    public RequestFinalResponse updateRequest(UUID requestId, RequestDto requestDTO) {
        Request request = requestRepository.findWithIncludeById(requestId);
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
            request.setLocation(requestDTO.getLocation() != null ? requestDTO.getLocation() : request.getLocation());
            request.setIsEmergency(requestDTO.isEmergency());
            request.setStatus(requestDTO.getStatus() != null ? requestDTO.getStatus() : request.getStatus());
            if (requestDTO.getTagIds() != null) {
                taggableService.updateTaggables(request.getId(), requestDTO.getTagIds(),TaggableType.REQUEST);
            } else {
                taggableService.updateTaggables(request.getId(), new ArrayList<>(),TaggableType.REQUEST);
            }
            objectAttachmentService.updateAttachments(request.getId(), requestDTO.getImageUrls(), TaggableType.REQUEST);
            objectAttachmentService.updateAttachments(request.getId(), requestDTO.getVideoUrls(), TaggableType.REQUEST);
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
        requestRepository.deleteById(requestId);
    }
}