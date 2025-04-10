package fptu.fcharity.service.manage.project;

import fptu.fcharity.dto.project.ToProjectDonationDto;
import fptu.fcharity.entity.*;
import fptu.fcharity.entity.ToProjectDonation;
import fptu.fcharity.repository.TransactionHistoryRepository;
import fptu.fcharity.repository.WalletRepository;
import fptu.fcharity.repository.manage.project.ProjectRepository;
import fptu.fcharity.repository.manage.project.ToProjectDonationRepository;
import fptu.fcharity.repository.manage.user.UserRepository;
import fptu.fcharity.response.project.ToProjectDonationResponse;
import fptu.fcharity.service.WalletService;
import fptu.fcharity.utils.constants.TransactionType;
import fptu.fcharity.utils.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ToProjectDonationService {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;
    @Autowired
    private ToProjectDonationRepository toProjectDonationRepository;
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

    public ToProjectDonationResponse createDonation(ToProjectDonationDto donationDto) {
        // Create a new donation
        ToProjectDonation donation = new ToProjectDonation();
        if(donationDto.getProjectId()!=null){
            Project p = projectRepository.findWithEssentialById(donationDto.getProjectId());
            donation.setProject(p);
        }
        if(donationDto.getUserId()!=null){
            User u = userRepository.findWithEssentialById(donationDto.getUserId());
            donation.setUser(u);
        }
        donation.setAmount(donationDto.getAmount());
        donation.setMessage(donationDto.getMessage());
        donation.setDonationTime(Instant.now());
        donation.setDonationStatus("VERIFIED");
        ToProjectDonation t = toProjectDonationRepository.save(donation);
        updateWalletBalanceDonate(donation);
        return new ToProjectDonationResponse(t);
    }
    private void updateWalletBalanceDonate(ToProjectDonation t){
        Wallet userWallet = t.getUser().getWalletAddress();
        userWallet.setBalance(userWallet.getBalance().subtract(t.getAmount()));
        walletRepository.save(userWallet);

        Wallet projectWallet = t.getProject().getWalletAddress();
        projectWallet.setBalance(projectWallet.getBalance().add(t.getAmount()));
        walletRepository.save(projectWallet);

        TransactionHistory transactionHistory = new TransactionHistory(
                userWallet,
                t.getAmount(),
                TransactionType.DONATE_PROJECT,
                t.getDonationTime(),
                projectWallet
        );
        transactionHistoryRepository.save(transactionHistory);
    }

    public List<ToProjectDonationResponse> getAllDonationsOfProject(UUID projectId) {
        List<ToProjectDonation> l = toProjectDonationRepository.findByProjectId(projectId);
        return l.stream().map(ToProjectDonationResponse::new).toList();
    }
}
