package fptu.fcharity.service.organization;

import fptu.fcharity.entity.Organization;

import java.util.List;
import java.util.UUID;

public interface OrganizationService {
    List<Organization> getAll();
    Organization getById(UUID id);
    Organization save(Organization organization);
    Organization update(Organization organization);
    void delete(UUID id);
}
