package fptu.fcharity.service;

import fptu.fcharity.dao.OrganizationMemberDAO;
import fptu.fcharity.entity.OrganizationMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OrganizationMemberServiceImpl implements OrganizationMemberService {
    private final OrganizationMemberDAO organizationMemberDAO;

    @Autowired
    public OrganizationMemberServiceImpl(OrganizationMemberDAO organizationMemberDAO) {
        this.organizationMemberDAO = organizationMemberDAO;
    }

    @Override
    public List<OrganizationMember> findAll() {
        return organizationMemberDAO.getAll();
    }

    @Override
    public OrganizationMember findById(UUID id) {
        return organizationMemberDAO.getById(id);
    }

    @Override
    @Transactional
    public OrganizationMember save(OrganizationMember organizationMember) {
        return organizationMemberDAO.save(organizationMember);
    }

    @Override
    @Transactional
    public OrganizationMember update(OrganizationMember organizationMember) {
        return organizationMemberDAO.update(organizationMember);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        organizationMemberDAO.delete(id);
    }
}
