package fptu.fcharity.service.organization;

import fptu.fcharity.entity.Organization;
import fptu.fcharity.entity.OrganizationMember;
import fptu.fcharity.repository.manage.organization.OrganizationMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fptu.fcharity.entity.OrganizationMember.OrganizationMemberRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizationMemberServiceImpl implements OrganizationMemberService {
    private final OrganizationMemberRepository organizationMemberRepository;

    @Autowired
    public OrganizationMemberServiceImpl(OrganizationMemberRepository organizationMemberRepository) {
        this.organizationMemberRepository = organizationMemberRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationMember> findAll() {
        return organizationMemberRepository.findAll();
    }

    @Override
    public Optional<OrganizationMember> findById(UUID id) {
        return organizationMemberRepository.findOrganizationMemberByMembershipId(id);
    }

    @Override
    public OrganizationMemberRole findUserRoleInOrganization(UUID userId, UUID organizationId) {
        return  organizationMemberRepository.findOrganizationMemberByUserUserIdAndOrganizationOrganizationId(userId, organizationId).getMemberRole();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationMember> findOrganizationMemberByOrganization(Organization organization) {
        return organizationMemberRepository.findOrganizationMemberByOrganization(organization);
    }

    @Override
    @Transactional
    public OrganizationMember save(OrganizationMember organizationMember) {
        return organizationMemberRepository.save(organizationMember);
    }

    @Override
    @Transactional
    public OrganizationMember update(OrganizationMember organizationMember) {
        return organizationMemberRepository.save(organizationMember);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        organizationMemberRepository.deleteById(id);
    }

    public void updateMemberRole(OrganizationMember organizationMember, UUID id) {}
}
