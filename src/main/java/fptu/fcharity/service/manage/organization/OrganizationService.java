package fptu.fcharity.service.manage.organization;

import fptu.fcharity.dto.organization.OrganizationDto;
import fptu.fcharity.entity.Organization;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface OrganizationService {
    List<OrganizationDto> findAll();
    OrganizationDto findById(UUID id);
    Organization findEntityById(UUID id);
    OrganizationDto createOrganization(OrganizationDto organizationDto) throws IOException;
    OrganizationDto updateOrganization(OrganizationDto organizationDto) throws IOException;
    void deleteOrganization(UUID id);
    List<OrganizationDto> getOrganizationsByCeoOrManager(UUID ceoManagerId);
    OrganizationDto getOrganizationByOrganizationIdAndCeoOrManager(UUID id, UUID ceoManagerId);
    Organization getMyOrganization(UUID userId);
}
