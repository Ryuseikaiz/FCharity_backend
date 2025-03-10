package fptu.fcharity.service;

import fptu.fcharity.dto.request.RequestDto;
import fptu.fcharity.entity.*;
import fptu.fcharity.repository.*;
import fptu.fcharity.response.request.RequestResponse;
import fptu.fcharity.utils.constants.TaggableType;
import fptu.fcharity.utils.exception.ApiRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TaggableService taggableService;

    public RequestService(TaggableRepository _taggableRepository,
                          RequestRepository requestRepository,
                          UserRepository userRepository,
                          CategoryRepository categoryRepository,
                          TaggableService taggableService) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.taggableService = taggableService;
    }

    public List<RequestResponse> getAllRequests() {
        List<Request> requestList =  requestRepository.findAllWithInclude();
        return  requestList.stream()
                .map(request -> new RequestResponse(request,taggableService.getTagsOfObject(request.getId(),TaggableType.REQUEST)))
                .toList();
    }

    public RequestResponse getRequestById(UUID requestId) {
        Request request =  requestRepository.findWithIncludeById(requestId);
       return new RequestResponse(request,taggableService.getTagsOfObject(request.getId(), TaggableType.REQUEST));
    }

    public RequestResponse createRequest(RequestDto requestDTO) {
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
           return new RequestResponse(request,taggableService.getTagsOfObject(request.getId(), TaggableType.REQUEST));
       }catch(Exception e){
           throw new ApiRequestException(e.getMessage());
       }
    }

    public RequestResponse updateRequest(UUID requestId, RequestDto requestDTO) {
        Request request = requestRepository.findById(requestId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()).getBody();
        if (request != null) {
            User user = userRepository.findById(requestDTO.getUserId())
                    .orElseThrow(() -> new ApiRequestException("User not found"));

            Category category = categoryRepository.findById(requestDTO.getCategoryId())
                    .orElseThrow(() -> new ApiRequestException("Category not found"));

            request.setUser(user);
            request.setCategory(category);
            request.setTitle(requestDTO.getTitle() != null ? requestDTO.getTitle() : request.getTitle());
            request.setContent(requestDTO.getContent() != null ? requestDTO.getContent() : request.getContent());
            request.setPhone(requestDTO.getPhone() != null ? requestDTO.getPhone() : request.getPhone());
            request.setEmail(requestDTO.getEmail() != null ? requestDTO.getEmail() : request.getEmail());
            request.setLocation(requestDTO.getLocation() != null ? requestDTO.getLocation() : request.getLocation());
            request.setIsEmergency(requestDTO.isEmergency());
            request.setStatus(requestDTO.getStatus() != null ? requestDTO.getStatus() : request.getStatus());
            taggableService.updateTaggables(request.getId(), requestDTO.getTagIds(),TaggableType.REQUEST);
             requestRepository.save(request);
            return new RequestResponse(request,taggableService.getTagsOfObject(request.getId(), TaggableType.REQUEST));
        }
        return null;
    }

    public void deleteRequest(UUID requestId) {
        if(!requestRepository.existsById(requestId)){
            throw new ApiRequestException("Request not found");
        }
        requestRepository.deleteById(requestId);
    }
}

