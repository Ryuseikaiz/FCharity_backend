package fptu.fcharity.dao;

import fptu.fcharity.entity.OrganizationMember;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class OrganizationMemberDAOImpl implements OrganizationMemberDAO {
    private final EntityManager entityManager;

    @Autowired
    public OrganizationMemberDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<OrganizationMember> getAll() {
        return entityManager.createQuery("from OrganizationMember", OrganizationMember.class).getResultList();
    }

    @Override
    public OrganizationMember getById(UUID id) {
        return entityManager.find(OrganizationMember.class, id);
    }

    @Override
    public OrganizationMember save(OrganizationMember organizationMember) {
        return entityManager.merge(organizationMember);
    }

    @Override
    public OrganizationMember update(OrganizationMember organizationMember) {
        return entityManager.merge(organizationMember);
    }

    @Override
    public void delete(UUID id) {
        entityManager.remove(id);
    }
}
