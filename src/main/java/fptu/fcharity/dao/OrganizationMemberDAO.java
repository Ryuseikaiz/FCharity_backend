package fptu.fcharity.dao;

import fptu.fcharity.entity.OrganizationMember;

import java.util.List;
import java.util.UUID;

public interface OrganizationMemberDAO {
    List<OrganizationMember> getAll();
    OrganizationMember getById(UUID id);
    OrganizationMember save(OrganizationMember organizationMember);
    OrganizationMember update(OrganizationMember organizationMember);
    void delete(UUID id);
}
