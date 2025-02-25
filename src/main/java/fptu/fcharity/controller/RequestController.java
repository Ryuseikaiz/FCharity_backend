package fptu.fcharity.controller;

import fptu.fcharity.dto.request.RequestDto;
import fptu.fcharity.entity.Request;
import fptu.fcharity.response.request.RequestResponse;
import fptu.fcharity.service.RequestService;
import fptu.fcharity.utils.mapper.RequestResponseMapper;
import fptu.fcharity.utils.mapper.UserResponseMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/requests")
public class RequestController {
    private final RequestService requestService;
    private final RequestResponseMapper requestResponseMapper;

    public RequestController(RequestService requestService, RequestResponseMapper requestResponseMapper) {
        this.requestService = requestService;
        this.requestResponseMapper = requestResponseMapper;
    }

    @GetMapping
    public ResponseEntity<List<Request>> getAllRequests() {
        return ResponseEntity.ok(requestService.getAllRequests());
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> getRequestById(@PathVariable UUID id) {
        Request request = requestService.getRequestById(id);
        return ResponseEntity.ok(request);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRequest(@RequestBody RequestDto requestDto) {
        Request request = requestService.createRequest(requestDto);
        return ResponseEntity.ok(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Request> updateRequest(@PathVariable UUID id, @RequestBody RequestDto requestDTO) {
        return ResponseEntity.ok(requestService.updateRequest(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable UUID id) {
        requestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }
}