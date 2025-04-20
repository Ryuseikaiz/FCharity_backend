package fptu.fcharity.service.manage.organization;

import fptu.fcharity.dto.organization.OrganizationDTO;
import fptu.fcharity.entity.*;
import fptu.fcharity.response.organization.RecommendedOrganizationResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface OrganizationService {
    public List<RecommendedOrganizationResponse> getRecommendedOrganizations();
    List<OrganizationDTO> findAll();
    List<OrganizationDTO> getMyOrganizations();
    OrganizationDTO findById(UUID id);
    OrganizationDTO createOrganization(OrganizationDTO organizationDTO);

    OrganizationDTO updateOrganization(OrganizationDTO organizationDTO);

    void deleteOrganizationByCeo(UUID organizationId);
    void deleteOrganizationByAdmin(UUID organizationId);

    List<OrganizationDTO> getOrganizationsByManagerId(UUID managerId);
    OrganizationDTO getOrganizationByCeoId(UUID ceoId);
    OrganizationDTO getOrganizationByOrganizationIdAndManagerId(UUID organizationId, UUID managerId);
}