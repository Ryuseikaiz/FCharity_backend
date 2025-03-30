package fptu.fcharity.service.manage.organization;

import fptu.fcharity.dto.organization.OrganizationDto;
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
    List<OrganizationDto> getOrganizationsByManager(UUID managerId);
    OrganizationDto getOrganizationByIdAndManager(UUID id, UUID managerId);
    Organization getMyOrganization(UUID userId);
}
