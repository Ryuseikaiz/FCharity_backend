package fptu.fcharity.dto.admindashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
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
}
