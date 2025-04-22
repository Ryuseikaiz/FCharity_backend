package fptu.fcharity.dto.organization;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationMemberDTO {
    private UUID userId;
    private UUID organizationId;
}
