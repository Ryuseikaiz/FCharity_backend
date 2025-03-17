package fptu.fcharity.repository;

import fptu.fcharity.entity.OrganizationUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationUserRoleRepository extends JpaRepository<OrganizationUserRole, UUID> {

    List<OrganizationUserRole> findByIdUserIdAndRoleId(UUID userId, UUID roleId);
    Optional<OrganizationUserRole> findByIdUserIdAndIdOrganizationId(UUID userId, UUID organizationId);
    boolean existsByIdUserIdAndIdOrganizationId(UUID userId, UUID organizationId);
    boolean existsByIdUserIdAndIdOrganizationIdAndRoleId(UUID userId, UUID organizationId, UUID roleId);
}
