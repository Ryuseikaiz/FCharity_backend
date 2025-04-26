package fptu.fcharity.controller.manage.project;

import fptu.fcharity.response.project.ExtraFundRequestDto;
import fptu.fcharity.response.project.ExtraFundRequestResponse;
import fptu.fcharity.service.manage.project.ProjectExtraFundRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/extra-fund-requests")
public class ExtraFundRequestController {

    @Autowired
    private ProjectExtraFundRequestService extraFundRequestService;

    @PostMapping
    public ResponseEntity<?> createExtraFundRequest(@RequestBody ExtraFundRequestDto dto) {
        ExtraFundRequestResponse response = extraFundRequestService.createExtraFundRequest(dto);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/update")
    public ResponseEntity<?> updateExtraFundRequest(@RequestBody ExtraFundRequestDto dto) {
        ExtraFundRequestResponse response = extraFundRequestService.createExtraFundRequest(dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/approve")
    public ResponseEntity<?> approveExtraFundRequest(@RequestBody ExtraFundRequestDto dto) {
        ExtraFundRequestResponse approvedResponse = extraFundRequestService.approveExtraFundRequest(dto);
        return ResponseEntity.ok(approvedResponse);
    }

    @PutMapping("/reject")
    public ResponseEntity<?> rejectExtraFundRequest(@RequestBody ExtraFundRequestDto dto) {
        ExtraFundRequestResponse rejectedResponse = extraFundRequestService.rejectExtraFundRequest(dto);
        return ResponseEntity.ok(rejectedResponse);
    }
}
