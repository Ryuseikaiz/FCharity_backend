package fptu.fcharity.service.organization;

import fptu.fcharity.entity.Organization;
import fptu.fcharity.entity.OrganizationImage;
import fptu.fcharity.repository.manage.organization.OrganizationImageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizationImageService {
    private final OrganizationImageRepository organizationImageRepository;

    public OrganizationImageService(OrganizationImageRepository organizationImageRepository) {
        this.organizationImageRepository = organizationImageRepository;
    }

    public OrganizationImage save(OrganizationImage organizationImage) {
        organizationImageRepository.save(organizationImage);
        return organizationImage;
    }

    public List<OrganizationImage> findAll() {
        return organizationImageRepository.findAll();
    }

    public Optional<OrganizationImage> findById(UUID id) {
        return organizationImageRepository.findById(id);
    }

    public void deleteById(UUID id) {
        organizationImageRepository.deleteById(id);
    }

    public List<OrganizationImage> findByOrganization(Organization organization) {
        return organizationImageRepository.findOrganizationImageByOrganizationId(organization.getOrganizationId());
    }
}
