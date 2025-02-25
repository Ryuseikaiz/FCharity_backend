package fptu.fcharity.controller;

import fptu.fcharity.dto.request.RequestDTO;
import fptu.fcharity.entity.Request;
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
    public ResponseEntity<List<Request>> getAllRequests() {
        return ResponseEntity.ok(requestService.getAllRequests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Request> getRequestById(@PathVariable UUID id) {
        return ResponseEntity.ok(requestService.getRequestById(id));
    }

    @PostMapping
    public ResponseEntity<Request> createRequest(@RequestBody RequestDTO requestDTO) {
        return ResponseEntity.ok(requestService.createRequest(requestDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Request> updateRequest(@PathVariable UUID id, @RequestBody RequestDTO requestDTO) {
        return ResponseEntity.ok(requestService.updateRequest(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable UUID id) {
        requestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }
}