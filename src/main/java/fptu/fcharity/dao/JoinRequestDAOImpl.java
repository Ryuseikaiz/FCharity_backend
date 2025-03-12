package fptu.fcharity.dao;

import fptu.fcharity.entity.InviteJoinRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JoinRequestDAOImpl implements JoinRequestDAO {
    private final EntityManager entityManager;

    public JoinRequestDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public InviteJoinRequest createJoinRequest(InviteJoinRequest joinRequest) {
        return entityManager.merge(joinRequest);
    }

    @Override
    public InviteJoinRequest updateJoinRequest(InviteJoinRequest joinRequest) {
        return entityManager.merge(joinRequest);
    }

    @Override
    public void deleteJoinRequest(InviteJoinRequest joinRequest) {
        entityManager.remove(joinRequest);
    }

    @Override
    public List<InviteJoinRequest> getAllJoinRequests() {
        return entityManager.createQuery("from InviteJoinRequest", InviteJoinRequest.class).getResultList();
    }

    @Override
    public List<InviteJoinRequest> getAllJoinRequestsByOrganizationId(UUID organizationId) {
        TypedQuery<InviteJoinRequest> query = entityManager.createQuery("from InviteJoinRequest where organizationId = :organizationId", InviteJoinRequest.class);
        query.setParameter("organizationId", organizationId);
        return query.getResultList();
    }

    @Override
    public InviteJoinRequest getJoinRequestById(UUID id) {
        return entityManager.find(InviteJoinRequest.class, id);
    }

    @Override
    public List<InviteJoinRequest> getAllJoinRequestsByUserId(UUID userId) {
        TypedQuery<InviteJoinRequest> query = entityManager.createQuery("from InviteJoinRequest where userId = :userId", InviteJoinRequest.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
}
