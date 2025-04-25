package fptu.fcharity.dto.admindashboard;

import fptu.fcharity.dto.organization.UploadedFileDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OrganizationDTO {
    private UUID id;
    private String organizationName;
    private String email;
    private String phoneNumber;
    private String address;
    private String organizationDescription;
    private Instant startTime;
    private Instant shutdownDay;
    private String organizationStatus;
    private UUID ceoId;
    private String reason;
    private List<UploadedFileDTO> documents;
}
