package fptu.fcharity.service.organization;

import fptu.fcharity.dto.organization.OrganizationUserRoleDTO;

import java.util.UUID;

public interface OrganizationManagerService {
    OrganizationUserRoleDTO addManager(UUID currentUserId, UUID organizationId, UUID userId);
    OrganizationUserRoleDTO updateManager(UUID currentUserId, UUID organizationId, UUID userId, UUID newRoleId);
    void removeManager(UUID currentUserId, UUID organizationId, UUID userId);
    boolean hasPermission(UUID userId, UUID organizationId);
}
