package fptu.fcharity.service.manage.organization;


import fptu.fcharity.dto.organization.OrganizationDto;
import fptu.fcharity.entity.Organization;
import fptu.fcharity.entity.User;
import fptu.fcharity.entity.Wallet;
import fptu.fcharity.repository.WalletRepository;
import fptu.fcharity.repository.manage.organization.OrganizationRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.utils.exception.ApiRequestException;
import fptu.fcharity.utils.mapper.OrganizationDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OrganizationService {
    @Autowired
    private OrganizationDtoMapper organizationDtoMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private OrganizationRepository organizationRepository;

    @Transactional(readOnly = true)
    public List<Organization> getAll() {
        return organizationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Organization getById(UUID id) {
        return organizationRepository.findById(id).orElse(null);
    }
    public void takeObject(Organization organization, OrganizationDto organizationDto) {
        if (organizationDto.getCeoId() != null) {
            User user = userRepository.findById(organizationDto.getCeoId())
                    .orElseThrow(() -> new ApiRequestException("Không tìm thấy Leader"));
            organization.setCeo(user);
        }

        if (organizationDto.getWalletId() != null) {
            Wallet wallet = walletRepository.findById(organizationDto.getWalletId())
                    .orElseThrow(() -> new ApiRequestException("Không tìm thấy Wallet"));
            organization.setWalletAddress(wallet);
        }
    }
    @Transactional
    public Organization save(OrganizationDto organizationDto) {
        Organization organization = organizationDtoMapper.toEntity(organizationDto);
        takeObject(organization, organizationDto);
        return organizationRepository.save(organization);
    }

    @Transactional
    public Organization update(Organization organization) {
        return organizationRepository.save(organization);
    }

    @Transactional
    public void delete(UUID id) {
        organizationRepository.deleteById(id);
    }
}
