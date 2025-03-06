package fptu.fcharity.controller;

import fptu.fcharity.dto.request.RequestDto;
import fptu.fcharity.entity.Taggable;
import fptu.fcharity.response.request.RequestFinalResponse;
import fptu.fcharity.service.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/requests")
public class RequestController {
    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    public ResponseEntity<List<RequestFinalResponse>> getAllRequests() {
        List<RequestFinalResponse> requests = requestService.getAllRequests();
        return ResponseEntity.ok(requests);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> getRequestById(@PathVariable UUID id) {
        RequestFinalResponse request = requestService.getRequestById(id);
        return ResponseEntity.ok(request);
    }

    @GetMapping(value = "/{id}/tags", produces = "application/json")
    public ResponseEntity<?> getTagsOfRequest(@PathVariable UUID id) {
        List<Taggable> tags = requestService.getTagsOfRequest(id);
        return ResponseEntity.ok(tags);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRequest(@RequestBody RequestDto requestDto) {
        RequestFinalResponse request = requestService.createRequest(requestDto);
        return ResponseEntity.ok(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RequestFinalResponse> updateRequest(@PathVariable UUID id, @RequestBody RequestDto requestDTO) {
        RequestFinalResponse request = requestService.updateRequest(id, requestDTO);
        return ResponseEntity.ok(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable UUID id) {
        requestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }
}