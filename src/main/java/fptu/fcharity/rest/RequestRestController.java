package fptu.fcharity.rest;

import fptu.fcharity.entity.Request;
import fptu.fcharity.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/request")
public class RequestRestController {
    private final RequestService requestService;

    @Autowired
    public RequestRestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping("/requests")
    public List<Request> getRequests() {
        return requestService.getAll();
    }

    @GetMapping("/requests/{request_id}")
    public Request getRequest(@PathVariable UUID request_id) {
        return requestService.getById(request_id);
    }

    @PostMapping("/requests")
    public Request createRequest(@RequestBody Request request) {
        return requestService.save(request);
    }

    @PutMapping("/requests")
    public Request updateRequest(@RequestBody Request request) {
        return requestService.update(request);
    }

    @DeleteMapping("/requests/{request_id}")
    public void deleteRequest(@PathVariable UUID request_id) {
        requestService.delete(request_id);
    }

}
