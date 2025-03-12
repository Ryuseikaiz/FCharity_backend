package fptu.fcharity.service.organization;

import fptu.fcharity.dao.OrganizationDAO;
import fptu.fcharity.dto.organization.OrganizationDTO;
import fptu.fcharity.entity.Organization;
import fptu.fcharity.entity.OrganizationUserRole;
import fptu.fcharity.entity.User;
import fptu.fcharity.repository.OrganizationRepository;
import fptu.fcharity.repository.OrganizationUserRoleRepository;
import fptu.fcharity.repository.RoleRepository;
import fptu.fcharity.repository.UserRepository;
import fptu.fcharity.service.filestorage.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrganizationServiceImpl implements OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final OrganizationUserRoleRepository organizationUserRoleRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public OrganizationServiceImpl(
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            OrganizationUserRoleRepository organizationUserRoleRepository,
            FileStorageService fileStorageService,
            RoleRepository roleRepository
    )
    {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
        this.organizationUserRoleRepository = organizationUserRoleRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Organization> getAllOrganizations() {
        return organizationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Organization getById(UUID id) {
        return organizationRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Organization createOrganization(Organization organization) throws IOException {
// Xử lý ảnh nếu có
        if (organization.getPictures() != null && organization.getPictures().startsWith("data:image")) {
            String imagePath = fileStorageService.storeBase64Image(organization.getPictures());
            organization.setPictures(imagePath);
        }

        // Gán CEO nếu có ceoId
        if (organization.getCeoId() != null) {
            Optional<User> ceo = userRepository.findById(organization.getCeoId().getUserId());
            ceo.ifPresent(organization::setCeoId);
        }

        return organizationRepository.save(organization);
    }

    @Override
    @Transactional
    public Organization updateOrganization(Organization organization) throws IOException {
        Optional<Organization> existingOrg = organizationRepository.findById(organization.getOrganizationId());
        if (!existingOrg.isPresent()) {
            throw new RuntimeException("Organization not found");
        }

        Organization orgToUpdate = existingOrg.get();

        orgToUpdate.setOrganizationName(organization.getOrganizationName());
        orgToUpdate.setEmail(organization.getEmail());
        orgToUpdate.setPhoneNumber(organization.getPhoneNumber());
        orgToUpdate.setAddress(organization.getAddress());
        orgToUpdate.setOrganizationDescription(organization.getOrganizationDescription());


        if (organization.getPictures() != null && organization.getPictures().startsWith("data:image")) {
            String imagePath = fileStorageService.storeBase64Image(organization.getPictures());
            orgToUpdate.setPictures(imagePath);
        } else if (organization.getPictures() == null || organization.getPictures().isEmpty()) {
            orgToUpdate.setPictures(null); // Xóa ảnh nếu frontend gửi rỗng
        }


        if (organization.getCeoId() != null && organization.getCeoId().getUserId() != null) {
            Optional<User> ceo = userRepository.findById(organization.getCeoId().getUserId());
            ceo.ifPresent(orgToUpdate::setCeoId);
        }

        return organizationRepository.save(orgToUpdate);
    }


    @Override
    @Transactional
    public void deleteOrganization(UUID organizationId) {
        organizationRepository.deleteById(organizationId);
    }

    @Override
    public List<OrganizationDTO> getOrganizationsByManager(UUID managerId) {
        UUID managerRoleId = roleRepository.findByName("Manager")
                .orElseThrow(() -> new RuntimeException("Role Manager not found"))
                .getRoleId();

        List<OrganizationUserRole> roles = organizationUserRoleRepository.findByIdUserIdAndRoleId(managerId, managerRoleId);

        List<UUID> organizationIds = roles.stream().map(role -> role.getId().getOrganizationId()).collect(Collectors.toList());

        List<Organization> organizations = organizationRepository.findAllById(organizationIds);

        return organizations.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public OrganizationDTO getOrganizationByIdAndManager(UUID organizationId, UUID userId) {
        UUID managerRoleId = roleRepository.findByName("Manager").orElseThrow(() -> new RuntimeException("Role Manager not found"))
                .getRoleId();

        boolean isManager = organizationUserRoleRepository.existsByIdUserIdAndIdOrganizationIdAndRoleId(userId, organizationId, managerRoleId);

        if (!isManager) {
            return null;
        }

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
        return convertToDTO(organization);
    }

    private OrganizationDTO convertToDTO(Organization organization) {
        OrganizationDTO dto = new OrganizationDTO();
        dto.setOrganizationId(organization.getOrganizationId().toString());
        dto.setOrganizationName(organization.getOrganizationName());
        dto.setEmail(organization.getEmail());
        dto.setPhoneNumber(organization.getPhoneNumber());
        dto.setAddress(organization.getAddress());
        dto.setOrganizationDescription(organization.getOrganizationDescription());
        dto.setPictures(organization.getPictures());
        dto.setOrganizationStatus(organization.getOrganizationStatus());
        return dto;
    }
}
