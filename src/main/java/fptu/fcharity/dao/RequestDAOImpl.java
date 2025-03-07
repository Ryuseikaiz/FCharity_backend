package fptu.fcharity.dao;

import fptu.fcharity.entity.Request;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class RequestDAOImpl implements RequestDAO {
    private final EntityManager entityManager;

    public RequestDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Request> getAll() {
        return entityManager.createQuery("from Request", Request.class).getResultList();
    }

    @Override
    public Request getById(UUID id) {
        return entityManager.find(Request.class, id);
    }

    @Override
    public Request save(Request request) {
        return entityManager.merge(request);
    }

    @Override
    public Request update(Request request) {
        return entityManager.merge(request);
    }

    @Override
    public void delete(UUID id) {
        entityManager.remove(entityManager.find(Request.class, id));
    }
}
