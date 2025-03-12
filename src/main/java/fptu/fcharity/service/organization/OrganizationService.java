package fptu.fcharity.service.organization;

import fptu.fcharity.entity.Organization;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface OrganizationService {
    List<Organization> getAllOrganizations();
    Organization getById(UUID id);
    Organization createOrganization(Organization organization) throws IOException;
    Organization updateOrganization(Organization organization) throws IOException;
    void deleteOrganization(UUID id);
}
