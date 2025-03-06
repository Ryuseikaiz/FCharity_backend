package fptu.fcharity.service;

import fptu.fcharity.dto.admindashboard.OrganizationDTO;
import fptu.fcharity.entity.Organization;
//import fptu.fcharity.entity.Organization.OrganizationStatus;
import fptu.fcharity.repository.ManageOrganizationRepository;
import fptu.fcharity.utils.exception.ApiRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static fptu.fcharity.utils.constants.RequestStatus.APPROVED;

@Service
@RequiredArgsConstructor
public class ManageOrganizationService {
    private final ManageOrganizationRepository organizationRepository;

    // Lấy danh sách tổ chức
    public List<OrganizationDTO> getAllOrganizations() {
        return organizationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Lấy tổ chức theo ID
    public OrganizationDTO getOrganizationById(UUID orgId) {
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ApiRequestException("Organization not found with ID: " + orgId));
        return convertToDTO(organization);
    }

    // Xóa tổ chức
    @Transactional
    public void deleteOrganization(UUID orgId) {
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ApiRequestException("Organization not found with ID: " + orgId));
        organizationRepository.delete(organization);
    }

    // Duyệt tổ chức từ Pending → Active
//    @Transactional
//    public void approveOrganization(UUID orgId) {
//        Organization organization = organizationRepository.findById(orgId)
//                .orElseThrow(() -> new ApiRequestException("Organization not found with ID: " + orgId));
//
//        if (organization.getOrganizationStatus() == OrganizationStatus.ACTIVE) {
//            throw new ApiRequestException("Organization is already active.");
//        }
//
//        organization.setOrganizationStatus(OrganizationStatus.ACTIVE);
//        organizationRepository.save(organization);
//    }
    @Transactional
    public void approveOrganization(UUID orgId) {
        Organization organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ApiRequestException("Organization not found with ID: " + orgId));

        if (organization.getOrganizationStatus().equals(APPROVED)) {
            throw new ApiRequestException("Organization is already active.");
        }

        organization.setOrganizationStatus(APPROVED);
        organizationRepository.save(organization);
    }

    // Chuyển đổi từ Entity → DTO
    private OrganizationDTO convertToDTO(Organization organization) {
        return new OrganizationDTO(
                organization.getId(),
                organization.getOrganizationName(),
                organization.getEmail(),
                organization.getPhoneNumber(),
                organization.getAddress(),
                organization.getOrganizationDescription(),
                organization.getPictures(),
                organization.getStartTime(),
                organization.getShutdownDay(),
                organization.getOrganizationStatus(),
                organization.getCeo() != null ? organization.getCeo().getId() : null
        );
    }
}
