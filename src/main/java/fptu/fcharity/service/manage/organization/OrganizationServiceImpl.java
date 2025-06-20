package fptu.fcharity.service.manage.organization;

import fptu.fcharity.dto.organization.OrganizationDTO;
import fptu.fcharity.dto.organization.OrganizationRankingDTO;
import fptu.fcharity.dto.organization.UserDTO;
import fptu.fcharity.dto.organization.WalletDTO;
import fptu.fcharity.entity.*;
import fptu.fcharity.entity.OrganizationMember.OrganizationMemberRole;
import fptu.fcharity.repository.WalletRepository;
import fptu.fcharity.repository.manage.organization.OrganizationImageRepository;
import fptu.fcharity.repository.manage.organization.OrganizationMemberRepository;
import fptu.fcharity.repository.manage.organization.OrganizationRepository;
import fptu.fcharity.repository.manage.organization.OrganizationRequestRepository;
import fptu.fcharity.repository.manage.project.ProjectRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.response.organization.RecommendedOrganizationResponse;
import fptu.fcharity.utils.constants.OrganizationStatus;
import fptu.fcharity.utils.constants.project.ProjectStatus;
import fptu.fcharity.utils.exception.ApiRequestException;
import fptu.fcharity.utils.mapper.organization.OrganizationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrganizationServiceImpl implements OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final OrganizationImageRepository organizationImageRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final OrganizationMapper organizationMapper;
    @Autowired
    private final SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private OrganizationRequestRepository organizationRequestRepository;


    @Autowired
    public OrganizationServiceImpl(
            OrganizationRepository organizationRepository,
            OrganizationImageRepository organizationImageRepository,
            UserRepository userRepository,
            OrganizationMemberRepository organizationMemberRepository,
            WalletRepository walletRepository,
            OrganizationMapper organizationMapper,
            SimpMessagingTemplate simpMessagingTemplate
    )
    {
        this.organizationRepository = organizationRepository;
        this.organizationImageRepository = organizationImageRepository;
        this.userRepository = userRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.walletRepository = walletRepository;
        this.organizationMapper = organizationMapper;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }
    @Override
    @Transactional(readOnly = true)
    public List<RecommendedOrganizationResponse> getRecommendedOrganizations (){
        User requestUser = userRepository.findByEmail(getAuthentication().getName()).orElseThrow(() -> new ApiRequestException("User not found"));
        List<Organization> organizations = organizationRepository.findAll().stream().filter(organization -> {
            return organizationMemberRepository.findOrganizationMemberByUserIdAndOrganizationOrganizationId(requestUser.getId(), organization.getOrganizationId()) == null;
        }).filter(organization -> {
            OrganizationRequest isExisted = organizationRequestRepository.findByOrganizationOrganizationIdAndUserId( organization.getOrganizationId(), requestUser.getId());
            if (isExisted != null &&
                    (isExisted.getStatus() == OrganizationRequest.OrganizationRequestStatus.Pending
                            || isExisted.getStatus() == OrganizationRequest.OrganizationRequestStatus.Approved)
            )
                return false;
            return true;
        }).toList();

        List<RecommendedOrganizationResponse> results = organizations.stream().map(organization -> {
            int totalMembers = organizationMemberRepository.findByOrganizationOrganizationId(organization.getOrganizationId()).size();
            int totalProjects = projectRepository.findByOrganizationOrganizationId(organization.getOrganizationId()).size();
            int totalCompletedProjects = projectRepository.findByOrganizationOrganizationIdAndProjectStatus(organization.getOrganizationId(), ProjectStatus.FINISHED).size();

            RecommendedOrganizationResponse recommendedOrganizationResponse = new RecommendedOrganizationResponse();
            recommendedOrganizationResponse.setOrganizationId(organization.getOrganizationId());
            recommendedOrganizationResponse.setOrganizationName(organization.getOrganizationName());
            recommendedOrganizationResponse.setOrganizationDescription(organization.getOrganizationDescription());
            recommendedOrganizationResponse.setStatus(organization.getOrganizationStatus());
            recommendedOrganizationResponse.setBackgroundUrl(organization.getBackgroundUrl());

            recommendedOrganizationResponse.setTotalMembers(totalMembers);
            recommendedOrganizationResponse.setTotalProjects(totalProjects);
            recommendedOrganizationResponse.setTotalCompletedProjects(totalCompletedProjects);

            return recommendedOrganizationResponse;
        }).toList();
        System.out.println("🧊🧊result in recommended organizations: " + results);

        return results;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationRankingDTO> getOrganizationsRanking() {
        List<Organization> organizations = organizationRepository.findAll();

        List<OrganizationRankingDTO> result = organizations.stream().map(organization -> {
            int totalMembers = organizationMemberRepository.findByOrganizationOrganizationId(organization.getOrganizationId()).size();
            int totalProjects = projectRepository.findByOrganizationOrganizationIdAndProjectStatus(organization.getOrganizationId(), "FINISHED").size();
            Wallet orgWallet = walletRepository.findById(organization.getWalletAddress().getId()).orElseThrow(() -> new ApiRequestException("Cannot find wallet"));
            BigDecimal orgWalletBalance = orgWallet.getBalance();
            OrganizationRankingDTO organizationRankingDTO = new OrganizationRankingDTO();

            organizationRankingDTO.setOrganizationId(organization.getOrganizationId());
            organizationRankingDTO.setOrganizationName(organization.getOrganizationName());
            organizationRankingDTO.setBackgroundUrl(organization.getBackgroundUrl());
            organizationRankingDTO.setEmail(organization.getEmail());

            organizationRankingDTO.setNumberOfMembers(totalMembers);
            organizationRankingDTO.setNumberOfProjects(totalProjects);
            organizationRankingDTO.setTotalFunding(orgWalletBalance);
            organizationRankingDTO.setOrganizationStatus(organization.getOrganizationStatus());

            return organizationRankingDTO;
        }).toList();

        System.out.println("⚓⚓ranking data: " + result);
        return result;
    }

    @Override
    @Transactional(readOnly = true)  // OK
    public List<OrganizationDTO> findAll() {
        List<Organization> organizations = organizationRepository.findAll();
        return organizations.stream().map(this::convertOrganizationToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationDTO> getMyOrganizations() {
        User user = userRepository.findByEmail(getAuthentication().getName()).orElseThrow(() -> new RuntimeException("User not found"));
        List<OrganizationMember> member = organizationMemberRepository
                .findOrganizationMemberByUserId(
                        user.getId()).stream().filter(organizationMember -> organizationMember.getMemberRole() == OrganizationMemberRole.MEMBER)
                .toList();
        List<Organization> organizations = member.stream().map(m -> organizationRepository.findById(m.getOrganization().getOrganizationId()).orElseThrow(() -> new RuntimeException("One of / All Organizations not found!"))).toList();
        return organizations.stream().map(this::convertOrganizationToDTO).toList();
    }

    @Override  // OK
    @Transactional(readOnly = true)   // OK
    public OrganizationDTO findById(UUID id) {
        Organization organization = organizationRepository.findById(id).orElseThrow(() -> new ApiRequestException("Organization not found"));
        return organizationMapper.toDTO(organization);
    }

    @Override
    @Transactional   // OK
    public OrganizationDTO createOrganization(OrganizationDTO organizationDTO) {
        User ceo = userRepository.findByEmail(getAuthentication().getName()).orElseThrow(() -> new RuntimeException("Anonymous user are not allowed to create organization"));
        if (organizationMemberRepository.findOrganizationMemberByUserId(ceo.getId()).stream().anyMatch(organizationMember -> organizationMember.getMemberRole() == OrganizationMemberRole.CEO)) {
            throw new ApiRequestException("You have already created an organization, cannot create any more organizations!");
        }
        Organization organization = organizationMapper.toEntity(organizationDTO);
        organization.setCeo(ceo);

        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.valueOf(0));
        Wallet savedWallet =  walletRepository.save(wallet);

        organization.setWalletAddress(savedWallet);
        organization.setOrganizationStatus(OrganizationStatus.PENDING);
        organization.setStartTime(Instant.now());
        Organization organizationSaved = organizationRepository.save(organization);

        OrganizationMember organizationMember = new OrganizationMember();
        organizationMember.setOrganization(organizationSaved);
        organizationMember.setUser(ceo);
        organizationMember.setMemberRole(OrganizationMemberRole.CEO);
        organizationMember.setJoinDate(Instant.now());

        organizationMemberRepository.save(organizationMember);

        organizationDTO.setOrganizationId(organizationSaved.getOrganizationId());
        simpMessagingTemplate.convertAndSend("/topic/organization-notifications", "User " + ceo.getEmail() + " has created a new organization.");
        return organizationDTO;
    }

    @Override
    @Transactional   // OK
    public OrganizationDTO updateOrganization(OrganizationDTO organizationDTO) {
        User user = userRepository.findByEmail(getAuthentication().getName()).orElseThrow(() -> new RuntimeException("Anonymous user are not allowed to create organization"));

        Organization organization =  organizationMapper.toEntity(organizationDTO);

        Optional<Organization> existingOrg = organizationRepository.findById(organization.getOrganizationId());
        if (existingOrg.isEmpty()) {
            throw new RuntimeException("Organization not found");
        }

        OrganizationMember.OrganizationMemberRole userRole = checkRole(organization.getOrganizationId(), user.getId());

        if (userRole == OrganizationMemberRole.CEO || userRole == OrganizationMemberRole.MANAGER) {
            if (organizationDTO.getCeo().getId() != null) {
                User newCeo = userRepository.findById(organizationDTO.getCeo().getId()).orElseThrow(() -> new RuntimeException("Ceo not found"));
                if (userRole == OrganizationMemberRole.CEO) {
                    organization.setCeo(newCeo);
                } else {
                    throw new ApiRequestException("You are not allowed to update CEO information");
                }
            } else {
                organization.setCeo(existingOrg.get().getCeo());
            }

            organizationRepository.save(organization);
        } else {
            throw new RuntimeException("You are not allowed to update this organization");
        }
        return convertOrganizationToDTO(organization);
    }


    @Override
    @Transactional
    public void deleteOrganizationByCeo(UUID organizationId) {
        User user = userRepository.findByEmail(getAuthentication().getName()).orElseThrow(() -> new RuntimeException("Anonymous user are not allowed to delete organization"));
        Organization organization = organizationRepository.findById(organizationId).orElseThrow(() -> new RuntimeException("Organization not found"));
        if (organizationMemberRepository.findOrganizationMemberByUserIdAndOrganizationOrganizationId(user.getId(), organizationId).getMemberRole() != OrganizationMemberRole.CEO) {
            throw new ApiRequestException("You are not allowed to delete this organization");
        }

        organization.setOrganizationStatus(OrganizationStatus.WATINGFORDELETION);
        organizationRepository.save(organization);
    }

    @Override
    @Transactional
    public void deleteOrganizationByAdmin(UUID organizationId) {
        User user = userRepository.findByEmail(getAuthentication().getName()).orElseThrow(() -> new RuntimeException("Anonymous user are not allowed to delete organization"));

        if (user.getUserRole() != User.UserRole.Admin) {
            throw new ApiRequestException("You are not allowed to approval deletion of this organization");
        }
        organizationRepository.deleteById(organizationId);
    }

    public List<OrganizationDTO> getOrganizationsByManagerId(UUID managerId) {
        return organizationMemberRepository.findOrganizationMemberByUserId(managerId)
                .stream()
                .filter(member -> member.getMemberRole() == OrganizationMemberRole.MANAGER)
                .map(member -> organizationRepository.findById(member.getOrganization().getOrganizationId())
                        .orElseThrow(() -> new RuntimeException("Organization not found")))
                .map(this::convertOrganizationToDTO)
                .toList();
    }

    @Override   //OK
    public OrganizationDTO getOrganizationByCeoId(UUID ceoId) {
        return organizationMemberRepository.findOrganizationMemberByUserId(ceoId)
                .stream()
                .filter(member ->  member.getMemberRole() == OrganizationMemberRole.CEO)
                .map(member -> organizationRepository.findById(member.getOrganization().getOrganizationId())
                        .orElseThrow(() -> new RuntimeException("Organization not found")))
                .map(organizationMapper::toDTO)
                .toList().getFirst();
    }

    @Override   // OK
    public OrganizationDTO getOrganizationByOrganizationIdAndManagerId(UUID organizationId, UUID managerId) {
        OrganizationMember organizationMember = organizationMemberRepository.findOrganizationMemberByUserIdAndOrganizationOrganizationId(managerId, organizationId);
        if (organizationMember == null || organizationMember.getMemberRole() != OrganizationMemberRole.MANAGER) {
            throw new RuntimeException("You are not the manager of this organization");
        }

        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        return convertOrganizationToDTO(organization);
    }

    private UserDTO convertUserToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFullName(user.getFullName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setAddress(user.getAddress());
        userDTO.setAvatar(user.getAvatar());
        userDTO.setUserRole(user.getUserRole());
        userDTO.setCreatedDate(user.getCreatedDate());
        userDTO.setUserStatus(user.getUserStatus());
        userDTO.setVerificationCode(user.getVerificationCode());
        userDTO.setVerificationCodeExpiresAt(user.getVerificationCodeExpiresAt());

        return userDTO;
    }

    private WalletDTO convertWalletToDTO(Wallet wallet) {
        WalletDTO walletDTO = new WalletDTO();
        walletDTO.setId(wallet.getId());
        walletDTO.setBalance(wallet.getBalance());
        return walletDTO;
    }

    private OrganizationDTO convertOrganizationToDTO(Organization organization) {
        OrganizationDTO DTO = new OrganizationDTO();

        DTO.setOrganizationId(organization.getOrganizationId());
        DTO.setOrganizationName(organization.getOrganizationName());
        DTO.setEmail(organization.getEmail());
        DTO.setPhoneNumber(organization.getPhoneNumber());
        DTO.setAddress(organization.getAddress());
        DTO.setWalletAddress(convertWalletToDTO(organization.getWalletAddress()));
        DTO.setOrganizationDescription(organization.getOrganizationDescription());
        DTO.setStartTime(organization.getStartTime());
        DTO.setShutdownDay(organization.getShutdownDay());
        DTO.setOrganizationStatus(organization.getOrganizationStatus());
        DTO.setCeo(convertUserToDTO(organization.getCeo()));
        return DTO;
    }

    private Organization convertToEntity(OrganizationDTO DTO) {
        Organization organization = new Organization();

        if (DTO.getOrganizationId() != null) {
            organization.setOrganizationId(DTO.getOrganizationId());
        }

        organization.setOrganizationName(DTO.getOrganizationName());
        organization.setOrganizationDescription(DTO.getOrganizationDescription());
        organization.setEmail(DTO.getEmail());
        organization.setPhoneNumber(DTO.getPhoneNumber());
        organization.setAddress(DTO.getAddress());
        if (DTO.getWalletAddress() != null && DTO.getWalletAddress().getId() != null) {
            organization.setWalletAddress(walletRepository.findById(DTO.getWalletAddress().getId()).orElseThrow(() -> new RuntimeException("Wallet not found")));
        }
        organization.setOrganizationStatus(OrganizationStatus.PENDING);
        organization.setStartTime(DTO.getStartTime());
        organization.setShutdownDay(DTO.getShutdownDay());
        return organization;
    }

    private void saveImages(UUID organizationId, String url, OrganizationImage.OrganizationImageType imageType) {
        OrganizationImage organizationImage = new OrganizationImage();
        organizationImage.setOrganizationId(organizationId);
        organizationImage.setImageUrl(url);
        organizationImage.setImageType(imageType);

        organizationImageRepository.save(organizationImage);
    }

    private OrganizationMember.OrganizationMemberRole checkRole(UUID organizationId, UUID userId) {
        return organizationMemberRepository.findOrganizationMemberByUserIdAndOrganizationOrganizationId(userId, organizationId).getMemberRole();
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}