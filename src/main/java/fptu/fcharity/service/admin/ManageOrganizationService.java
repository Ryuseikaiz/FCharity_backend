package fptu.fcharity.service.admin;

import fptu.fcharity.dto.admindashboard.OrganizationDTO;
import fptu.fcharity.entity.Organization;
import fptu.fcharity.repository.manage.organization.OrganizationRepository;
import fptu.fcharity.utils.exception.ApiRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

//import static fptu.fcharity.utils.constants.RequestStatus.APPROVED;
//import static fptu.fcharity.utils.constants.RequestStatus.HIDDEN;
import static fptu.fcharity.utils.constants.PostStatus.*;

@Service
@RequiredArgsConstructor
public class ManageOrganizationService {
    private final OrganizationRepository organizationRepository;

    public List<OrganizationDTO> getAllOrganizations() {
        return organizationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public OrganizationDTO getOrganizationById(UUID orgId) {
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ApiRequestException("Organization not found with ID: " + orgId));
        return convertToDTO(organization);
    }

    @Transactional
    public void deleteOrganization(UUID orgId) {
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ApiRequestException("Organization not found with ID: " + orgId));
        organizationRepository.delete(organization);
    }

//    @Transactional
//    public void unHideOrganization(UUID orgId) {
//        Organization organization = organizationRepository.findById(orgId)
//                .orElseThrow(() -> new ApiRequestException("Organization not found with ID: " + orgId));
//
//        if (organization.getOrganizationStatus().equals(APPROVED)) {
//            throw new ApiRequestException("Organization is already active.");
//        }
//
//        organization.setOrganizationStatus(APPROVED);
//        organizationRepository.save(organization);
//    }
    @Transactional
    public void banOrganization(UUID orgId) {
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ApiRequestException("Organization not found with ID: " + orgId));

        if (!APPROVED.equals(organization.getOrganizationStatus())) {
            throw new ApiRequestException("Only approved organizations can be hidden.");
        }

        organization.setOrganizationStatus(BANNED);
        organizationRepository.save(organization);
    }

    @Transactional
    public void unbanOrganization(UUID orgId) {
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ApiRequestException("Organization not found with ID: " + orgId));

        if (!BANNED.equals(organization.getOrganizationStatus())) {
            throw new ApiRequestException("Only approved organizations can be hidden.");
        }

        organization.setOrganizationStatus(APPROVED);
        organizationRepository.save(organization);
    }

    @Transactional
    public void activateOrganization(UUID orgId) {
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ApiRequestException("Organization not found with ID: " + orgId));

        if (APPROVED.equals(organization.getOrganizationStatus())) {
            throw new ApiRequestException("Organization is already active.");
        }

        if (!PENDING.equals(organization.getOrganizationStatus())) {
            throw new ApiRequestException("Only organizations in PENDING status can be activated.");
        }

        organization.setOrganizationStatus(APPROVED);
        organizationRepository.save(organization);
    }

    @Transactional
    public void rejectOrganization(UUID orgId) {
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ApiRequestException("Organization not found with ID: " + orgId));

        if (!PENDING.equals(organization.getOrganizationStatus())) {
            throw new ApiRequestException("Only organizations in PENDING status can be activated.");
        }

        organization.setOrganizationStatus(REJECTED);
        organizationRepository.save(organization);
    }

    private OrganizationDTO convertToDTO(Organization organization) {
        return new OrganizationDTO(
                organization.getId(),
                organization.getOrganizationName(),
                organization.getEmail(),
                organization.getPhoneNumber(),
                organization.getAddress(),
                organization.getOrganizationDescription(),
                organization.getStartTime(),
                organization.getShutdownDay(),
                organization.getOrganizationStatus(),
                organization.getCeo() != null ? organization.getCeo().getId() : null
        );
    }
}
