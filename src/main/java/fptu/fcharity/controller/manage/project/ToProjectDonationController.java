package fptu.fcharity.controller.manage.project;

import fptu.fcharity.dto.project.ToProjectDonationDto;
import fptu.fcharity.entity.ToProjectDonation;
import fptu.fcharity.response.project.ToProjectDonationResponse;
import fptu.fcharity.service.manage.project.ToProjectDonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects/donations")
public class ToProjectDonationController {
    @Autowired
    private ToProjectDonationService toProjectDonationService;
    @PostMapping("/create")
    public ResponseEntity<?> createDonation(@RequestBody ToProjectDonationDto donationDto) {
       ToProjectDonationResponse res =  toProjectDonationService.createDonation(donationDto);
        return ResponseEntity.ok(res);
    }
    @GetMapping("/{projectId}")
    public ResponseEntity<?> getAllDonations(@PathVariable UUID projectId) {
        List<ToProjectDonationResponse> res =  toProjectDonationService.getAllDonationsOfProject(projectId);
        return ResponseEntity.ok(res);
    }
}
