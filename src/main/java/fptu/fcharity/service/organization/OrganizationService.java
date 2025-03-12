package fptu.fcharity.service.organization;

import fptu.fcharity.dto.organization.OrganizationDTO;
import fptu.fcharity.entity.Organization;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface OrganizationService {
    List<Organization> getAllOrganizations();
    Organization getById(UUID id);
    Organization createOrganization(Organization organization) throws IOException;
    Organization updateOrganization(Organization organization) throws IOException;
    void deleteOrganization(UUID id);
    List<OrganizationDTO> getOrganizationsByManager(UUID managerId);
    OrganizationDTO getOrganizationByIdAndManager(UUID id, UUID managerId);
}
