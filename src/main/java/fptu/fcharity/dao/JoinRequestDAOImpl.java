package fptu.fcharity.dao;

import fptu.fcharity.entity.JoinRequest;
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
    public JoinRequest createJoinRequest(JoinRequest joinRequest) {
        return entityManager.merge(joinRequest);
    }

    @Override
    public JoinRequest updateJoinRequest(JoinRequest joinRequest) {
        return entityManager.merge(joinRequest);
    }

    @Override
    public void deleteJoinRequest(JoinRequest joinRequest) {
        entityManager.remove(joinRequest);
    }

    @Override
    public List<JoinRequest> getAllJoinRequests() {
        return entityManager.createQuery("from JoinRequest", JoinRequest.class).getResultList();
    }

    @Override
    public List<JoinRequest> getAllJoinRequestsByOrganizationId(UUID organizationId) {
        TypedQuery<JoinRequest> query = entityManager.createQuery("from JoinRequest where organizationId = :organizationId", JoinRequest.class);
        query.setParameter("organizationId", organizationId);
        return query.getResultList();
    }

    @Override
    public JoinRequest getJoinRequestById(UUID id) {
        return entityManager.find(JoinRequest.class, id);
    }

    @Override
    public List<JoinRequest> getAllJoinRequestsByUserId(UUID userId) {
        TypedQuery<JoinRequest> query = entityManager.createQuery("from JoinRequest where userId = :userId", JoinRequest.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
}
