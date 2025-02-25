package fptu.fcharity.service;

import fptu.fcharity.dto.request.RequestDto;
import fptu.fcharity.entity.Category;
import fptu.fcharity.entity.Request;
import fptu.fcharity.entity.Tag;
import fptu.fcharity.entity.User;
import fptu.fcharity.utils.exception.ApiRequestException;
import fptu.fcharity.repository.CategoryRepository;
import fptu.fcharity.repository.RequestRepository;
import fptu.fcharity.repository.TagRepository;
import fptu.fcharity.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public RequestService(RequestRepository requestRepository, UserRepository userRepository, CategoryRepository categoryRepository, TagRepository tagRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
    }

    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    public Request getRequestById(UUID requestId) {
        return requestRepository.findWithCategoryAndTagById(requestId);
    }

    public Request createRequest(RequestDto requestDTO) {
       try{
           User user = userRepository.findById(requestDTO.getUserId())
                   .orElseThrow(() -> new ApiRequestException("User not found"));

           Category category = categoryRepository.findById(requestDTO.getCategoryId())
                   .orElseThrow(() -> new ApiRequestException("Category not found"));
           Tag tag = tagRepository.findById(requestDTO.getTagId())
                   .orElseThrow(() -> new ApiRequestException("Tag not found"));
           Request request = new Request(UUID.randomUUID(),
                   user, requestDTO.getTitle(), requestDTO.getContent(),
                   requestDTO.getPhone(), requestDTO.getEmail(),
                   requestDTO.getLocation(), requestDTO.getAttachment(),
                   requestDTO.isEmergency(), category, tag);
           return requestRepository.save(request);
       }catch(Exception e){
           throw new ApiRequestException(e.getMessage());
       }
    }

    public Request updateRequest(UUID requestId, RequestDto requestDTO) {
        Request request = requestRepository.findById(requestId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()).getBody();
        if (request != null) {
            User user = userRepository.findById(requestDTO.getUserId())
                    .orElseThrow(() -> new ApiRequestException("User not found"));

            Category category = categoryRepository.findById(requestDTO.getCategoryId())
                    .orElseThrow(() -> new ApiRequestException("Category not found"));

            Tag tag = tagRepository.findById(requestDTO.getTagId())
                    .orElseThrow(() -> new ApiRequestException("Tag not found"));

            request.setUser(user);
            request.setTitle(requestDTO.getTitle());
            request.setContent(requestDTO.getContent());
            request.setPhone(requestDTO.getPhone());
            request.setEmail(requestDTO.getEmail());
            request.setLocation(requestDTO.getLocation());
            request.setAttachment(requestDTO.getAttachment());
            request.setIsEmergency(requestDTO.isEmergency());
            request.setCategory(category);
            request.setTag(tag);
            request.setRequestStatus(requestDTO.getStatus());
            return requestRepository.save(request);
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