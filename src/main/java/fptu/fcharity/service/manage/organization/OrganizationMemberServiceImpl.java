package fptu.fcharity.service.manage.organization;

import fptu.fcharity.entity.Organization;
import fptu.fcharity.entity.OrganizationMember;
import fptu.fcharity.repository.manage.organization.OrganizationMemberRepository;
import fptu.fcharity.repository.manage.organization.OrganizationRepository;
import fptu.fcharity.response.organization.OrganizationMemberResponse;
import fptu.fcharity.utils.exception.ApiRequestException;
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
    private final OrganizationRepository organizationRepository;

    @Autowired
    public OrganizationMemberServiceImpl(OrganizationMemberRepository organizationMemberRepository, OrganizationRepository organizationRepository) {
        this.organizationMemberRepository = organizationMemberRepository;
        this.organizationRepository = organizationRepository;
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
        return  organizationMemberRepository.findOrganizationMemberByUserIdAndOrganizationOrganizationId(userId, organizationId).getMemberRole();
    }

//    @Override
//    @Transactional(readOnly = true)
    public List<OrganizationMemberResponse> findOrganizationMemberByOrganizationIDD(UUID id) {
        List<OrganizationMember> organizationMembers = organizationMemberRepository.findOrganizationMemberByOrganizationId(id);
        List<OrganizationMemberResponse> organizationMembersr = organizationMembers.stream().map(OrganizationMemberResponse::new).toList();
    return organizationMembersr;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationMemberResponse> findOrganizationMemberByOrganizationId(UUID organizationId) {
        List<OrganizationMember> organizationMembers = organizationMemberRepository.findOrganizationMemberByOrganizationId(organizationId);
        List<OrganizationMemberResponse> organizationMembersr = organizationMembers.stream().map(OrganizationMemberResponse::new).toList();
        return organizationMembersr;
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
