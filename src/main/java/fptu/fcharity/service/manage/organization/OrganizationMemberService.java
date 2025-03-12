package fptu.fcharity.service.manage.organization;

import fptu.fcharity.entity.OrganizationMember;
import fptu.fcharity.repository.manage.organization.OrganizationMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizationMemberService {
    private final OrganizationMemberRepository organizationMemberRepository;

    public OrganizationMemberService(OrganizationMemberRepository organizationMemberRepository) {
        this.organizationMemberRepository = organizationMemberRepository;
    }

    @Transactional(readOnly = true)
    public List<OrganizationMember> findAll() {
        return organizationMemberRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<OrganizationMember> findById(UUID id) {
        return organizationMemberRepository.findById(id);
    }

    @Transactional
    public OrganizationMember save(OrganizationMember organizationMember) {
        return organizationMemberRepository.save(organizationMember);
    }

    @Transactional
    public OrganizationMember update(OrganizationMember organizationMember) {
        return organizationMemberRepository.save(organizationMember);
    }

    @Transactional
    public void delete(UUID id) {
        organizationMemberRepository.deleteById(id);
    }
}
