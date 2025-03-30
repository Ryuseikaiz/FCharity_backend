package fptu.fcharity.service.manage.organization;

import fptu.fcharity.dto.organization.OrganizationDto;
import fptu.fcharity.entity.Organization;
import fptu.fcharity.entity.OrganizationMember;
import fptu.fcharity.entity.OrganizationMember.OrganizationMemberRole;
import fptu.fcharity.entity.User;
import fptu.fcharity.repository.manage.organization.OrganizationMemberRepository;
import fptu.fcharity.repository.manage.organization.OrganizationRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizationServiceImpl implements OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final UserRepository userRepository;

    @Autowired
    public OrganizationServiceImpl(
            OrganizationRepository organizationRepository,
            UserRepository userRepository,
            OrganizationMemberRepository organizationMemberRepository
    )
    {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.organizationMemberRepository = organizationMemberRepository;
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
//        if (organization.getPictures() != null && organization.getPictures().startsWith("data:image")) {
//            String imagePath = fileStorageService.storeBase64Image(organization.getPictures());
//            organization.setPictures(imagePath);
//        }

        // Gán CEO nếu có ceoId
        if (organization.getCeo() != null) {
            Optional<User> ceo = userRepository.findById(organization.getCeo().getId());
            ceo.ifPresent(organization::setCeo);
        }

        return organizationRepository.save(organization);
    }

    @Override
    @Transactional
    public Organization updateOrganization(Organization organization) throws IOException {
        Optional<Organization> existingOrg = organizationRepository.findById(organization.getOrganizationId());
        if (existingOrg.isEmpty()) {
            throw new RuntimeException("Organization not found");
        }

        Organization orgToUpdate = existingOrg.get();

        orgToUpdate.setOrganizationName(organization.getOrganizationName());
        orgToUpdate.setEmail(organization.getEmail());
        orgToUpdate.setPhoneNumber(organization.getPhoneNumber());
        orgToUpdate.setAddress(organization.getAddress());
        orgToUpdate.setOrganizationDescription(organization.getOrganizationDescription());


//        if (organization.getPictures() != null && organization.getPictures().startsWith("data:image")) {
//            String imagePath = fileStorageService.storeBase64Image(organization.getPictures());
//            orgToUpdate.setPictures(imagePath);
//        } else if (organization.getPictures() == null || organization.getPictures().isEmpty()) {
//            orgToUpdate.setPictures(null); // Xóa ảnh nếu frontend gửi rỗng
//        }


        if (organization.getCeo() != null && organization.getCeo().getId() != null) {
            Optional<User> ceo = userRepository.findById(organization.getCeo().getId());
            ceo.ifPresent(orgToUpdate::setCeo);
        }

        return organizationRepository.save(orgToUpdate);
    }


    @Override
    @Transactional
    public void deleteOrganization(UUID organizationId) {
        organizationRepository.deleteById(organizationId);
    }

    @Override
    public List<OrganizationDto> getOrganizationsByManager(UUID managerId) {
        List<OrganizationMember> organizationMembers = organizationMemberRepository.findOrganizationMemberByUserId(managerId);

        List<OrganizationDto> organizations =
                organizationMemberRepository.findOrganizationMemberByUserId(managerId)
                        .stream()
                        .filter(member -> member.getMemberRole() == OrganizationMemberRole.MANAGER || member.getMemberRole() == OrganizationMemberRole.CEO)
                        .map(member -> organizationRepository.findById(member.getOrganization().getOrganizationId())
                                .orElseThrow(() -> new RuntimeException("Organization not found")))
                        .map(this::convertToDTO)
                        .toList();
        return organizations;
    }

    public OrganizationDto getOrganizationByIdAndManager(UUID organizationId, UUID userId) {
        OrganizationMember organizationMember = organizationMemberRepository.findOrganizationMemberByUserIdAndOrganizationOrganizationId(userId, organizationId);
        if (organizationMember == null || organizationMember.getMemberRole() == OrganizationMemberRole.MEMBER) {
            return null;
        }

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
        return convertToDTO(organization);
    }

    private OrganizationDto convertToDTO(Organization organization) {
        OrganizationDto dto = new OrganizationDto();
        dto.setId(organization.getOrganizationId());
        dto.setOrganizationName(organization.getOrganizationName());
        dto.setEmail(organization.getEmail());
        dto.setPhoneNumber(organization.getPhoneNumber());
        dto.setAddress(organization.getAddress());
        dto.setOrganizationDescription(organization.getOrganizationDescription());
        dto.setOrganizationStatus(organization.getOrganizationStatus());
        return dto;
    }
    public Organization getMyOrganization(UUID userId) {
        return organizationRepository.findOrganizationByUserId(userId);
    }
}
