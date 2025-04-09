package fptu.fcharity.dto.organization;

import fptu.fcharity.entity.OrganizationMember;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationMemberDTO {
    private UUID membershipId;
    private UserDTO user;
    private OrganizationDTO organization;
    private OrganizationMember.OrganizationMemberRole memberRole;
    private Instant joinDate;
    private Instant leaveDate;
}
