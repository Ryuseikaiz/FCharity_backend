package fptu.fcharity.repository;

import fptu.fcharity.entity.ObjectAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ObjectAttachmentRepository extends JpaRepository<ObjectAttachment, UUID> {
    List<ObjectAttachment> findByRequestId(UUID requestId);
    List<ObjectAttachment> findByProjectId(UUID projectId);
    List<ObjectAttachment> findByOrganizationId(UUID organizationId);
    List<ObjectAttachment> findByPhaseId(UUID phaseId);
    List<ObjectAttachment> findByPostId(UUID postId);
}