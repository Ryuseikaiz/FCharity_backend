package fptu.fcharity.service.manage.project;

import fptu.fcharity.dto.project.ToProjectDonationDto;
import fptu.fcharity.entity.*;
import fptu.fcharity.entity.ToProjectDonation;
import fptu.fcharity.repository.WalletRepository;
import fptu.fcharity.repository.manage.project.ProjectRepository;
import fptu.fcharity.repository.manage.project.ToProjectDonationRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.response.project.ToProjectDonationResponse;
import fptu.fcharity.service.HelpNotificationService;
import fptu.fcharity.service.WalletService;
import fptu.fcharity.utils.constants.TransactionType;
import fptu.fcharity.utils.constants.project.DonationStatus;
import fptu.fcharity.utils.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ToProjectDonationService {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private ToProjectDonationRepository toProjectDonationRepository;
    @Autowired
    private HelpNotificationService notificationService;
    @Transactional
//    public void takeObject(ToProjectDonation t, ToProjectDonationDto dto){
//        if(dto.getProjectId()!=null){
//            Project p = projectRepository.findWithEssentialById(dto.getProjectId());
//            t.setProject(p);
//        }
//        if(dto.getUserId()!=null){
//            User u = userRepository.findWithEssentialById(dto.getUserId());
//            t.setUser(u);
//        }
//    }
    public int hashUUIDToInt(UUID uuid) {
        try {
            String input = uuid.toString();

            // Hash bằng SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // Lấy 4 byte đầu để tạo ra int
            int result = 0;
            for (int i = 0; i < 4; i++) {
                result = (result << 8) | (hash[i] & 0xff);
            }

            // Đảm bảo kết quả là số dương
            return Math.abs(result);
        } catch (Exception e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }

    public ToProjectDonationResponse createDonation(ToProjectDonationDto donationDto) {
        ToProjectDonation donation = new ToProjectDonation();
        if(donationDto.getProjectId()!=null){
            Project p = projectRepository.findWithEssentialById(donationDto.getProjectId());
            donation.setProject(p);
        }
        if(donationDto.getUserId()!=null){
            User u = userRepository.findWithEssentialById(donationDto.getUserId());
            donation.setUser(u);
        }
        donation.setAmount(BigDecimal.valueOf(donationDto.getAmount()));
        donation.setMessage(donationDto.getMessage());
        donation.setDonationTime(Instant.now());
        donation.setDonationStatus(donationDto.getDonationStatus());
        donation.setOrderCode(donationDto.getOrderCode());
        ToProjectDonation t = toProjectDonationRepository.save(donation);

        return new ToProjectDonationResponse(t);
    }
    public ToProjectDonationResponse updateDonation(int orderCode,Instant transactionTime, String decision) {
        ToProjectDonation donation = toProjectDonationRepository.findByOrderCode(orderCode);
       if(decision.equals(DonationStatus.COMPLETED) || decision.equals(DonationStatus.CANCELLED)) {
           donation.setDonationStatus(decision);
           donation.setDonationTime(transactionTime);
           toProjectDonationRepository.save(donation);
           updateWalletBalanceDonate(donation);

           UUID projectId = donation.getProject().getId();
           Project project = projectRepository.findWithEssentialById(projectId);
           User leader = project.getLeader();

           if (leader != null) {
               notificationService.notifyUser(
                       leader,
                       "New donation received",
                       null,
                       "Your project \"" + project.getProjectName() + "\" has just received a new donation.",
                       "/projects/" + project.getId() +"/details"
               );
           }
       }

        return new ToProjectDonationResponse(donation);
    }
    private void updateWalletBalanceDonate(ToProjectDonation t){
        Wallet projectWallet = t.getProject().getWalletAddress();
        projectWallet.setBalance(projectWallet.getBalance().add(t.getAmount()));
        walletRepository.save(projectWallet);
    }

    public List<ToProjectDonationResponse> getAllDonationsOfProject(UUID projectId) {
        List<ToProjectDonation> l = toProjectDonationRepository.findByProjectId(projectId)
                .stream()
                .filter(donate -> Objects.equals(donate.getDonationStatus(), DonationStatus.COMPLETED))
                .collect(Collectors.toList());
        return l.stream().map(ToProjectDonationResponse::new).toList();
    }
}
