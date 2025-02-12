package fptu.fcharity.service;

import fptu.fcharity.dao.OrganizationDAO;
import fptu.fcharity.entity.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OrganizationServiceImpl implements OrganizationService {
    private final OrganizationDAO organizationDAO;

    @Autowired
    public OrganizationServiceImpl(OrganizationDAO organizationDAO)
    {
        this.organizationDAO = organizationDAO;
    }

    @Override
    public List<Organization> getAll() {
        return organizationDAO.getAll();
    }

    @Override
    public Organization getById(UUID id) {
        return organizationDAO.getById(id);
    }

    @Override
    @Transactional
    public Organization save(Organization organization) {
        organization.setOrganizationId(UUID.randomUUID());
        return organizationDAO.save(organization);
    }

    @Override
    @Transactional
    public Organization update(Organization organization) {
        return organizationDAO.update(organization);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        organizationDAO.delete(id);
    }
}
