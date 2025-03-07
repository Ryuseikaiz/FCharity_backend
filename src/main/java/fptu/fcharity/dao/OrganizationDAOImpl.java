package fptu.fcharity.dao;

import fptu.fcharity.entity.Organization;
import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class OrganizationDAOImpl implements OrganizationDAO {
    private final EntityManager entityManager;

    @Autowired
    OrganizationDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Organization> getAll() {
        System.out.println("Inside getAll method");
        List<Organization> result = entityManager.createQuery("from Organization", Organization.class).getResultList();
        return result;
    }

    @Override
    public Organization getById(UUID id) {
        System.out.println("Inside getById method in OrganizationDAOImpl");
        return entityManager.find(Organization.class, id);
    }

    @Override
    public Organization save(Organization organization) {
        return entityManager.merge(organization);
    }

    @Override
    public Organization update(Organization organization) {
        return entityManager.merge(organization);
    }

    @Override
    public void delete(UUID id) {
        entityManager.remove(entityManager.find(Organization.class, id));
    }
}
