package fptu.fcharity.repository.manage.organization;

import fptu.fcharity.entity.ToOrganizationDonation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ToOrganizationDonationRepository extends JpaRepository<ToOrganizationDonation, UUID> {
    ToOrganizationDonation findByOrderCode(int orderCode);

    List<ToOrganizationDonation> findByUserId(UUID userId);

    List<ToOrganizationDonation> findByOrganizationOrganizationId(UUID organizationId);
}
