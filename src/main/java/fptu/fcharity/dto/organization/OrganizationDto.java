package fptu.fcharity.dto.organization;

import lombok.*;

import java.time.Instant;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrganizationDto {
    private UUID id;
    private String organizationName;
    private String email;
    private String phoneNumber;
    private String address;
    private UUID walletId;
    private String organizationDescription;
    private Instant startTime;
    private Instant shutdownDay;
    private String organizationStatus;
    private UUID ceoId;
    private String avatarUrl;
    private String backgroundUrl;
}
