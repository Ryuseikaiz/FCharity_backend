package fptu.fcharity.service.manage.organization;

import fptu.fcharity.dto.organization.OrganizationDto;
import fptu.fcharity.entity.*;
import fptu.fcharity.entity.OrganizationMember.OrganizationMemberRole;
import fptu.fcharity.repository.WalletRepository;
import fptu.fcharity.repository.manage.organization.OrganizationImageRepository;
import fptu.fcharity.repository.manage.organization.OrganizationMemberRepository;
import fptu.fcharity.repository.manage.organization.OrganizationRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.utils.constants.OrganizationStatus;
import fptu.fcharity.utils.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizationServiceImpl implements OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final OrganizationImageRepository organizationImageRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;


    @Autowired
    public OrganizationServiceImpl(
            OrganizationRepository organizationRepository,
            OrganizationImageRepository organizationImageRepository,
            UserRepository userRepository,
            OrganizationMemberRepository organizationMemberRepository,
            WalletRepository walletRepository
    )
    {
        this.organizationRepository = organizationRepository;
        this.organizationImageRepository = organizationImageRepository;
        this.userRepository = userRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.walletRepository = walletRepository;
    }

    @Override
    @Transactional(readOnly = true)  // OK
    public List<OrganizationDto> findAll() {
        List<Organization> organizations = organizationRepository.findAll();
        List<OrganizationDto>  dtos =  organizations.stream().map(this::convertToDTO).toList();
        return dtos;

    }

    @Override  // OK
    @Transactional(readOnly = true)   // OK
    public OrganizationDto findById(UUID id) {
        Organization organization = organizationRepository.findById(id).orElseThrow(() -> new ApiRequestException("Organization not found"));
        return convertToDTO(organization);
    }

    @Override
    @Transactional(readOnly = true)   // OK
    public Organization findEntityById(UUID id) {
        return organizationRepository.findById(id).orElseThrow(() -> new ApiRequestException("Organization not found"));
    }

    @Override
    @Transactional   // OK
    public OrganizationDto createOrganization(OrganizationDto organizationDto) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User ceo = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RuntimeException("Anonymous user are not allowed to create organization"));

        Organization organization = convertToEntity(organizationDto);
        organization.setCeo(ceo);

        Wallet wallet = new Wallet();
        wallet.setBalance(0);
        Wallet savedWallet =  walletRepository.save(wallet);
        organization.setWalletAddress(savedWallet);

        Organization organizationSaved = organizationRepository.save(organization);

        saveImages(organizationSaved.getOrganizationId(), organizationDto.getAvatarUrl(), OrganizationImage.OrganizationImageType.Avatar);
        saveImages(organizationSaved.getOrganizationId(), organizationDto.getBackgroundUrl(), OrganizationImage.OrganizationImageType.Background);

        OrganizationMember organizationMember = new OrganizationMember();
        organizationMember.setOrganization(organizationSaved);
        organizationMember.setUser(ceo);
        organizationMember.setMemberRole(OrganizationMemberRole.CEO);
        organizationMember.setJoinDate(Instant.now());

        organizationMemberRepository.save(organizationMember);

        organizationDto.setOrganizationId(organizationSaved.getOrganizationId());

        return organizationDto;
    }

    @Override
    @Transactional   // OK
    public OrganizationDto updateOrganization(OrganizationDto organizationDto) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RuntimeException("Anonymous user are not allowed to create organization"));

        Organization organization = convertToEntity(organizationDto);

        System.out.println("Organization: " + organization);

        Optional<Organization> existingOrg = organizationRepository.findById(organization.getOrganizationId());
        if (existingOrg.isEmpty()) {
            throw new RuntimeException("Organization not found");
        }

        OrganizationMember.OrganizationMemberRole userRole = checkRole(organization.getOrganizationId(), user.getId());

        if (userRole == OrganizationMemberRole.CEO || userRole == OrganizationMemberRole.MANAGER) {
            if (organizationDto.getAvatarUrl() != null) {
                List<OrganizationImage> organizationImage = organizationImageRepository.findOrganizationImageByOrganizationIdAndImageType(organizationDto.getOrganizationId(), OrganizationImage.OrganizationImageType.Avatar);

                if (organizationImage != null && !organizationImage.isEmpty()) {
                    OrganizationImage image = organizationImage.getFirst();
                    image.setImageUrl(organizationDto.getAvatarUrl());
                    organizationImageRepository.save(image);
                } else {
                    OrganizationImage image = new OrganizationImage();
                    image.setOrganizationId(organizationDto.getOrganizationId());
                    image.setImageUrl(organizationDto.getAvatarUrl());
                    image.setImageType(OrganizationImage.OrganizationImageType.Avatar);
                    organizationImageRepository.save(image);
                }
            }

            if (organizationDto.getBackgroundUrl() != null) {
                List<OrganizationImage> organizationImage = organizationImageRepository.findOrganizationImageByOrganizationIdAndImageType(organizationDto.getOrganizationId(), OrganizationImage.OrganizationImageType.Background);

                if (organizationImage != null && !organizationImage.isEmpty()) {
                    OrganizationImage image = organizationImage.getFirst();
                    image.setImageUrl(organizationDto.getBackgroundUrl());
                    organizationImageRepository.save(image);
                } else {
                    OrganizationImage image = new OrganizationImage();
                    image.setOrganizationId(organizationDto.getOrganizationId());
                    image.setImageUrl(organizationDto.getBackgroundUrl());
                    image.setImageType(OrganizationImage.OrganizationImageType.Background);
                    organizationImageRepository.save(image);
                }
            }
            organizationRepository.save(organization);
        } else {
            throw new RuntimeException("You are not allowed to update this organization");
        }

        return convertToDTO(organization);
    }


    @Override
    @Transactional
    public void deleteOrganization(UUID organizationId) {
        organizationRepository.deleteById(organizationId);
    }

    @Override   //OK
    public List<OrganizationDto> getOrganizationsByCeoOrManager(UUID ceoManagerId) {

        return organizationMemberRepository.findOrganizationMemberByUserId(ceoManagerId)
                .stream()
                .filter(member -> member.getMemberRole() == OrganizationMemberRole.MANAGER || member.getMemberRole() == OrganizationMemberRole.CEO)
                .map(member -> organizationRepository.findById(member.getOrganization().getOrganizationId())
                        .orElseThrow(() -> new RuntimeException("Organization not found")))
                .map(this::convertToDTO)
                .toList();
    }

    @Override   // OK
    public OrganizationDto getOrganizationByOrganizationIdAndCeoOrManager(UUID organizationId, UUID ceoManagerId) {
        OrganizationMember organizationMember = organizationMemberRepository.findOrganizationMemberByUserIdAndOrganizationOrganizationId(ceoManagerId, organizationId);
        if (organizationMember == null || organizationMember.getMemberRole() == OrganizationMemberRole.MEMBER) {
            throw new RuntimeException("You are not allowed to get this organization");
        }

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        return convertToDTO(organization);
    }

    @Override
    public OrganizationDto getMyOrganization(UUID userId) {
        Organization organization = organizationRepository.findOrganizationByUserId(userId);
        return convertToDTO(organization);
    }

    private OrganizationDto convertToDTO(Organization organization) {
        OrganizationDto dto = new OrganizationDto();

        dto.setOrganizationId(organization.getOrganizationId());
        dto.setOrganizationName(organization.getOrganizationName());
        dto.setEmail(organization.getEmail());
        dto.setPhoneNumber(organization.getPhoneNumber());
        dto.setAddress(organization.getAddress());
        dto.setWalletId(organization.getWalletAddress().getId());
        dto.setOrganizationDescription(organization.getOrganizationDescription());
        dto.setStartTime(organization.getStartTime());
        dto.setShutdownDay(organization.getShutdownDay());
        dto.setOrganizationStatus(organization.getOrganizationStatus());
        dto.setCeoId(organization.getCeo().getId());

        String avatarUrl =
                organizationImageRepository
                        .findOrganizationImageByOrganizationIdAndImageType(
                                organization.getOrganizationId(),
                                OrganizationImage.OrganizationImageType.Avatar
                        ).getFirst().getImageUrl();
        String backgroundUrl =
                organizationImageRepository
                        .findOrganizationImageByOrganizationIdAndImageType(
                                organization.getOrganizationId(),
                                OrganizationImage.OrganizationImageType.Background
                        ).getFirst().getImageUrl();

        dto.setAvatarUrl(avatarUrl);
        dto.setBackgroundUrl(backgroundUrl);
        return dto;
    }

    private Organization convertToEntity(OrganizationDto dto) {
        Organization organization = new Organization();

        if (dto.getOrganizationId() != null) {
            organization.setOrganizationId(dto.getOrganizationId());
        }
        if (dto.getCeoId() != null) {
            organization.setCeo(userRepository.findById(dto.getCeoId()).orElseThrow(() -> new RuntimeException("Ceo not found")));
        }
        organization.setOrganizationName(dto.getOrganizationName());
        organization.setOrganizationDescription(dto.getOrganizationDescription());
        organization.setEmail(dto.getEmail());
        organization.setPhoneNumber(dto.getPhoneNumber());
        organization.setAddress(dto.getAddress());
        if (dto.getWalletId() != null) {
            organization.setWalletAddress(walletRepository.findById(dto.getWalletId()).orElseThrow(() -> new RuntimeException("Wallet not found")));
        }
        organization.setOrganizationStatus(OrganizationStatus.PENDING);
        organization.setStartTime(dto.getStartTime());
        organization.setShutdownDay(dto.getShutdownDay());
        return organization;
    }

    private OrganizationImage saveImages(UUID organizationId, String url, OrganizationImage.OrganizationImageType imageType) {
        OrganizationImage organizationImage = new OrganizationImage();
        organizationImage.setOrganizationId(organizationId);
        organizationImage.setImageUrl(url);
        organizationImage.setImageType(imageType);

        return organizationImageRepository.save(organizationImage);
    }

    private OrganizationMember.OrganizationMemberRole checkRole(UUID organizationId, UUID userId) {
        return organizationMemberRepository.findOrganizationMemberByUserIdAndOrganizationOrganizationId(userId, organizationId).getMemberRole();
    }
}