package fptu.fcharity.service.manage.project;

import fptu.fcharity.dto.project.SpendingDetailDto;
import fptu.fcharity.entity.Project;
import fptu.fcharity.entity.SpendingDetail;
import fptu.fcharity.entity.SpendingItem;
import fptu.fcharity.repository.manage.project.ProjectRepository;
import fptu.fcharity.repository.manage.project.SpendingDetailRepository;
import fptu.fcharity.repository.manage.project.SpendingItemRepository;
import fptu.fcharity.utils.constants.project.SpendingDetailResponse;
import fptu.fcharity.utils.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
public class SpendingDetailService {
    @Autowired
    private SpendingDetailRepository spendingDetailRepository;
    @Autowired
    private SpendingItemRepository spendingItemRepository;
    @Autowired
    private ProjectRepository projectRepository;

    public List<SpendingDetailResponse> getSpendingDetailsByProject(UUID projectId) {
        List<SpendingDetail> l =  spendingDetailRepository.findByProjectId(projectId);
        return l.stream().map(SpendingDetailResponse::new).toList();
    }

    public SpendingDetailResponse createSpendingDetail(SpendingDetailDto dto) {
        SpendingDetail spendingDetail = new SpendingDetail();
        SpendingItem spendingItem = spendingItemRepository.findById(dto.getSpendingItemId())
                .orElseThrow(() -> new ApiRequestException("Spending item not found"));
        spendingDetail.setSpendingItem(spendingItem);
        Project p = projectRepository.findWithEssentialById(dto.getProjectId());
        spendingDetail.setProject(p);
        spendingDetail.setAmount(dto.getAmount());
        spendingDetail.setProofImage(dto.getProofImage());
        spendingDetail.setDescription(dto.getDescription());
        spendingDetail.setTransactionTime(dto.getTransactionTime());
        SpendingDetail savedSpendingDetail = spendingDetailRepository.save(spendingDetail);
        return new SpendingDetailResponse(savedSpendingDetail);
    }
    public SpendingDetailResponse getSpendingDetailById(UUID id) {
        SpendingDetail spendingDetail = spendingDetailRepository.findById(id)
                .orElseThrow(() -> new ApiRequestException("Spending detail not found"));
        return new SpendingDetailResponse(spendingDetail);
    }
    public SpendingDetailResponse updateSpendingDetail(UUID id, SpendingDetailDto dto) {
        SpendingDetail spendingDetail = spendingDetailRepository.findById(id)
                .orElseThrow(() -> new ApiRequestException("Spending detail not found"));
        spendingDetail.setAmount(dto.getAmount());
        spendingDetail.setProofImage(dto.getProofImage());
        spendingDetail.setDescription(dto.getDescription());
        spendingDetail.setTransactionTime(dto.getTransactionTime());
        SpendingDetail updatedSpendingDetail = spendingDetailRepository.save(spendingDetail);
        return new SpendingDetailResponse(updatedSpendingDetail);
    }
    public void deleteSpendingDetail(UUID id) {
        SpendingDetail spendingDetail = spendingDetailRepository.findById(id)
                .orElseThrow(() -> new ApiRequestException("Spending detail not found"));
        spendingDetailRepository.delete(spendingDetail);
    }

    public List<SpendingDetailResponse> saveFromExcel(List<SpendingDetail> l) {
        List<SpendingDetail> list =  spendingDetailRepository.saveAll(l);
        return list.stream().map(SpendingDetailResponse::new).toList();
    }

    public void removeNonExtraFundSpendingDetails(UUID projectId) {
        List<SpendingDetail> allDetails = spendingDetailRepository.findByProjectId(projectId);
        if (allDetails != null && !allDetails.isEmpty()) {
            List<SpendingDetail> detailsToDelete = allDetails.stream()
                    .filter(detail -> detail.getDescription() == null || !detail.getDescription().contains("Extra funds for project"))
                    .toList(); // Hoặc .collect(Collectors.toList()) cho Java cũ hơn

            // Chỉ xóa nếu có detail cần xóa
            if (!detailsToDelete.isEmpty()) {
                spendingDetailRepository.deleteAll(detailsToDelete);
            }
        }
    }
}
