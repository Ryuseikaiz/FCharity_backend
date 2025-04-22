//package fptu.fcharity.response.organization;
//
//import fptu.fcharity.dto.organization.OrganizationDTO;
//import fptu.fcharity.entity.OrganizationMember;
//import fptu.fcharity.response.authentication.UserResponse;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.time.Instant;
//import java.util.UUID;
//@Setter
//@Getter
//@NoArgsConstructor
//public class OrganizationMemberResponse {
//    private UUID membershipId;
//
//    private UserResponse user;
//
//    private OrganizationDTO organization;
//
//    private String memberRole;
//
//    private Instant joinDate;
//
//    private Instant leaveDate;
//    public OrganizationMemberResponse(OrganizationMember organizationMember) {
//        this.membershipId = organizationMember.getMembershipId();
//        this.user = new UserResponse(organizationMember.getUser());
//        this.organization  = new OrganizationDTO(organizationMember.getOrganization());
//        this.memberRole = organizationMember.getMemberRole().name();
//        this.joinDate = organizationMember.getJoinDate();
//        this.leaveDate = organizationMember.getLeaveDate();
//    }
//}
