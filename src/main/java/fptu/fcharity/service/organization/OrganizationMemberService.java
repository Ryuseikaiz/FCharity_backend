package fptu.fcharity.service.organization;
import fptu.fcharity.entity.Organization;
import fptu.fcharity.entity.OrganizationMember;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationMemberService {
    public List<OrganizationMember> findAll();
    public Optional<OrganizationMember> findById(UUID id);
    public List<OrganizationMember> findOrganizationMemberByOrganization(Organization organization);
    public OrganizationMember save(OrganizationMember organizationMember);
    public OrganizationMember update(OrganizationMember organizationMember);
    public void delete(UUID id);
}
