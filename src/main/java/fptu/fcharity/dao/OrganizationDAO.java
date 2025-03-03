package fptu.fcharity.dao;

import fptu.fcharity.entity.Organization;

import java.util.List;
import java.util.UUID;

public interface OrganizationDAO {
    List<Organization> getAll();
    Organization getById(UUID id);
    Organization save(Organization organization);
    Organization update(Organization organization);
    void delete(UUID id);
}

