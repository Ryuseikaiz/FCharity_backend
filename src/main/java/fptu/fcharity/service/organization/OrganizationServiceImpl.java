package fptu.fcharity.service.organization;

import fptu.fcharity.dao.OrganizationDAO;
import fptu.fcharity.entity.Organization;
import fptu.fcharity.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OrganizationServiceImpl implements OrganizationService {
    private final OrganizationRepository organizationRepository;

    @Autowired
    public OrganizationServiceImpl(OrganizationRepository organizationRepository)
    {
        this.organizationRepository = organizationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Organization> getAll() {
        return organizationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Organization getById(UUID id) {
        return organizationRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Organization save(Organization organization) {
        return organizationRepository.save(organization);
    }

    @Override
    @Transactional
    public Organization update(Organization organization) {
        return organizationRepository.save(organization);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        organizationRepository.deleteById(id);
    }
}
