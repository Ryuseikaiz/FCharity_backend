package fptu.fcharity.service;

import fptu.fcharity.dto.request.RequestDto;
import fptu.fcharity.entity.*;
import fptu.fcharity.repository.*;
import fptu.fcharity.response.request.RequestFinalResponse;
import fptu.fcharity.utils.constants.TaggableType;
import fptu.fcharity.utils.exception.ApiRequestException;
import fptu.fcharity.utils.mapper.RequestResponseMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final TaggableRepository taggableRepository;
    private final RequestResponseMapper requestResponseMapper;

    public RequestService(TaggableRepository _taggableRepository,
                          RequestRepository requestRepository,
                          UserRepository userRepository,
                          CategoryRepository categoryRepository,
                          RequestResponseMapper requestResponseMapper,
                          TagRepository tagRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        taggableRepository = _taggableRepository;
        this.requestResponseMapper = requestResponseMapper;
    }

    public List<RequestFinalResponse> getAllRequests() {
        List<Request> requestList =  requestRepository.findAllWithInclude();
        return  requestList.stream()
                .map(request -> new RequestFinalResponse(request,getTagsOfRequest(request.getId())))
                .toList();
    }

    public RequestFinalResponse getRequestById(UUID requestId) {
        Request request =  requestRepository.findWithIncludeById(requestId);
        return new RequestFinalResponse(request,getTagsOfRequest(request.getId()));
    }
    public List<Taggable> getTagsOfRequest(UUID requestId) {
        return taggableRepository.findAllWithInclude().stream()
                .filter(taggable -> taggable.getTaggableId().equals(requestId) && taggable.getTaggableType().equals(TaggableType.REQUEST))
                .toList();
    }

    public void addRequestTags(UUID requestId, List<UUID> tagIds) {
        Request request = requestRepository.findById(requestId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()).getBody();
        for (UUID tagId : tagIds) {
            if (tagRepository.existsById(tagId)) {
                Tag tag = tagRepository.findById(tagId)
                        .orElseThrow(() -> new ApiRequestException("Tag not found"));
                Taggable taggable = new Taggable(tag,requestId, TaggableType.REQUEST);
                taggableRepository.save(taggable);
            }
        }}
    public void updateRequestTags(UUID requestId, List<UUID> tagIds) {
        List<Taggable> oldTags = taggableRepository.findAllWithInclude().stream()
                .filter(taggable -> taggable.getTaggableId().equals(requestId) && taggable.getTaggableType().equals(TaggableType.REQUEST))
                .toList();
        for (Taggable taggable: oldTags) {
            if(!tagIds.contains(taggable.getTag().getId())){taggableRepository.deleteById(taggable.getId());}
            tagIds.remove(taggable.getTag().getId());
        }
        addRequestTags(requestId, tagIds);
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
            addRequestTags(request.getId(), requestDTO.getTagIds());
            return new RequestFinalResponse(request,getTagsOfRequest(request.getId()));
        }catch(Exception e){
            throw new ApiRequestException(e.getMessage());
        }
    }

    public RequestFinalResponse updateRequest(UUID requestId, RequestDto requestDTO) {
        Request request = requestRepository.findWithIncludeById(requestId);
        if (request != null) {
           if(requestDTO.getCategoryId()!=null) {
               Category category = categoryRepository.findById(requestDTO.getCategoryId()).get();
               request.setCategory(category);
           }else{
                request.setCategory(null);
           }
            request.setTitle(requestDTO.getTitle() != null ? requestDTO.getTitle() : request.getTitle());
            request.setContent(requestDTO.getContent() != null ? requestDTO.getContent() : request.getContent());
            request.setPhone(requestDTO.getPhone() != null ? requestDTO.getPhone() : request.getPhone());
            request.setEmail(requestDTO.getEmail() != null ? requestDTO.getEmail() : request.getEmail());
            request.setLocation(requestDTO.getLocation() != null ? requestDTO.getLocation() : request.getLocation());
            request.setIsEmergency(requestDTO.isEmergency());
            request.setStatus(requestDTO.getStatus() != null ? requestDTO.getStatus() : request.getStatus());
            if(requestDTO.getTagIds()!=null) {
                updateRequestTags(request.getId(), requestDTO.getTagIds());
            }else{
                updateRequestTags(request.getId(),new ArrayList<UUID>());
        }
            requestRepository.save(request);
            return new RequestFinalResponse(request, getTagsOfRequest(request.getId()));
        }
        throw new ApiRequestException("Request not found");
    }

    public void deleteRequest(UUID requestId) {
        if(!requestRepository.existsById(requestId)){
            throw new ApiRequestException("Request not found");
        }
        requestRepository.deleteById(requestId);
    }
}