package fptu.fcharity.dto.organization;

import fptu.fcharity.entity.Organization;
import lombok.*;

import java.time.Instant;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrganizationDto {
    private UUID organizationId;
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
    public OrganizationDto(Organization organization){
        this.setOrganizationId(organization.getOrganizationId());
        this.setOrganizationName(organization.getOrganizationName());
        this.setEmail(organization.getEmail());
        this.setPhoneNumber(organization.getPhoneNumber());
        this.setAddress(organization.getAddress());
        this.setWalletId(organization.getWalletAddress().getId());
        this.setOrganizationDescription(organization.getOrganizationDescription());
        this.setStartTime(organization.getStartTime());
        this.setShutdownDay(organization.getShutdownDay());
        this.setOrganizationStatus(organization.getOrganizationStatus());
        this.setCeoId(organization.getCeo().getId());
    }
}
