package fptu.fcharity.service.organization;
import fptu.fcharity.entity.OrganizationMember;
import java.util.List;
import java.util.UUID;

public interface OrganizationMemberService {
    public List<OrganizationMember> findAll();
    public OrganizationMember findById(UUID id);
    public OrganizationMember save(OrganizationMember organizationMember);
    public OrganizationMember update(OrganizationMember organizationMember);
    public void delete(UUID id);
}
