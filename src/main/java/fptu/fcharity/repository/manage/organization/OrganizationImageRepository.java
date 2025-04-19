package fptu.fcharity.repository.manage.organization;

import fptu.fcharity.entity.OrganizationImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrganizationImageRepository extends JpaRepository<OrganizationImage, UUID> {
    List<OrganizationImage> findOrganizationImageByOrganizationId(UUID organizationId);

    List<OrganizationImage> findOrganizationImageByOrganizationIdAndImageType(UUID organizationId, OrganizationImage.OrganizationImageType imageType);
}
