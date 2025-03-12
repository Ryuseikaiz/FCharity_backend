package fptu.fcharity.repository.manage.organization;

import fptu.fcharity.entity.OrganizationMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, UUID> {
}
