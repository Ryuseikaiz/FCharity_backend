package fptu.fcharity.service.organization;

import fptu.fcharity.dto.organization.OrganizationUserRoleDTO;
import fptu.fcharity.entity.Organization;
import fptu.fcharity.entity.OrganizationUserRole;
import fptu.fcharity.entity.Role;
import fptu.fcharity.entity.User;
import fptu.fcharity.repository.OrganizationUserRoleRepository;
import fptu.fcharity.repository.RoleRepository;
import fptu.fcharity.repository.manage.organization.OrganizationRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrganizationManagerServiceImpl implements OrganizationManagerService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationUserRoleRepository organizationUserRoleRepository;

    public OrganizationManagerServiceImpl(UserRepository userRepository, RoleRepository roleRepository, OrganizationRepository organizationRepository, OrganizationUserRoleRepository organizationUserRoleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.organizationRepository = organizationRepository;
        this.organizationUserRoleRepository = organizationUserRoleRepository;
    }

    @Override
    public OrganizationUserRoleDTO addManager(UUID currentUserId, UUID organizationId, UUID userId) {
        if (!hasPermission(currentUserId, userId)) {
            throw new SecurityException("You do not have permission to add organization manager");
        }

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new SecurityException("Organization not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new SecurityException("User not found"));

        UUID managerRoleId = roleRepository.findByName("Manager")
                .orElseThrow(() -> new RuntimeException("Role Manager not found"))
                .getRoleId();

        if (organizationUserRoleRepository.existsByIdUserIdAndIdOrganizationId(userId, organizationId)) {
            throw new RuntimeException("User already has a role in this organization");
        }

        OrganizationUserRole managerRole = new OrganizationUserRole(userId, organizationId, managerRoleId);
        organizationUserRoleRepository.save(managerRole);

        return convertToDTO(managerRole);
    }

    @Override
    public OrganizationUserRoleDTO updateManager(UUID currentUserId, UUID organizationId, UUID userId, UUID newRoleId) {
        if (!hasPermission(currentUserId, userId)) {
            throw new SecurityException("You do not have permission to update organization manager");
        }

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        OrganizationUserRole existingRole = organizationUserRoleRepository
                .findByIdUserIdAndIdOrganizationId(userId, organizationId)
                .orElseThrow(() -> new RuntimeException("User is not assigned to this organization"));

        Role newRole = roleRepository.findById(newRoleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        existingRole.setRoleId(newRoleId);
        organizationUserRoleRepository.save(existingRole);

        return convertToDTO(existingRole);
    }

    @Override
    public void removeManager(UUID currentUserId, UUID organizationId, UUID userId) {
        if (!hasPermission(currentUserId, userId)) {
            throw new SecurityException("You do not have permission to remove organization manager");
        }

        Organization organization = organizationRepository.findById(organizationId).orElseThrow(() -> new RuntimeException("Organization not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        OrganizationUserRole managerRole = organizationUserRoleRepository.findByIdUserIdAndIdOrganizationId(userId, organizationId).orElseThrow(() -> new RuntimeException("User is not a manager of this organization"));

        organizationUserRoleRepository.delete(managerRole);
    }

    @Override
    public boolean hasPermission(UUID userId, UUID organizationId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new SecurityException("User not found"));

//        if (user.getUserRole().equals("Admin")) {
//            return true;
//        }
        return true;

//        UUID ceoRoleId = roleRepository.findByName("CEO").orElseThrow(() -> new RuntimeException("Role CEO not found")).getRoleId();
//
//        return organizationRepository.findById(organizationId)
//                .map(org -> org.getCeoId().equals(userId) || organizationUserRoleRepository.existsByIdUserIdAndIdOrganizationIdAndRoleId(userId, organizationId, ceoRoleId))
//                .orElse(false);
    }

    private OrganizationUserRoleDTO convertToDTO(OrganizationUserRole role) {
        OrganizationUserRoleDTO dto = new OrganizationUserRoleDTO();
        dto.setUserId(role.getUserId());
        dto.setOrganizationId(role.getOrganizationId());
        dto.setRoleId(role.getRoleId());
        return dto;
    }
}
