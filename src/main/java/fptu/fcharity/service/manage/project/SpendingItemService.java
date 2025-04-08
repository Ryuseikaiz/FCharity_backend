package fptu.fcharity.service.manage.project;

import fptu.fcharity.dto.project.SpendingItemDto;
import fptu.fcharity.entity.SpendingItem;
import fptu.fcharity.entity.SpendingPlan;
import fptu.fcharity.repository.manage.project.SpendingItemRepository;
import fptu.fcharity.repository.manage.project.SpendingPlanRepository;
import fptu.fcharity.response.project.SpendingItemResponse;
import fptu.fcharity.utils.exception.ApiRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SpendingItemService {
    @Autowired
    private SpendingItemRepository spendingItemRepository;

    @Autowired
    private SpendingPlanRepository spendingPlanRepository;

    public SpendingItemResponse createSpendingItem(SpendingItemDto dto) {
        SpendingPlan plan = spendingPlanRepository.findById(dto.getSpendingPlanId())
                .orElseThrow(() -> new ApiRequestException("Spending plan not found"));

        SpendingItem item = new SpendingItem();
        item.setSpendingPlan(plan);
        item.setItemName(dto.getItemName());
        item.setEstimatedCost(dto.getEstimatedCost());
        item.setNote(dto.getNote());
        item.setCreatedDate(Instant.now());

        return toResponse(spendingItemRepository.save(item));
    }

    public SpendingItemResponse getSpendingItemById(UUID id) {
        SpendingItem item = spendingItemRepository.findById(id)
                .orElseThrow(() -> new ApiRequestException("Spending item not found"));
        return toResponse(item);
    }

    public SpendingItemResponse updateSpendingItem(UUID id, SpendingItemDto dto) {
        SpendingItem item = spendingItemRepository.findById(id)
                .orElseThrow(() -> new ApiRequestException("Spending item not found"));

        if (dto.getItemName() != null) item.setItemName(dto.getItemName());
        if (dto.getEstimatedCost() != null) item.setEstimatedCost(dto.getEstimatedCost());
        if (dto.getNote() != null) item.setNote(dto.getNote());

        item.setUpdatedDate(Instant.now());

        return toResponse(spendingItemRepository.save(item));
    }

    public void deleteSpendingItem(UUID id) {
        SpendingItem item = spendingItemRepository.findById(id)
                .orElseThrow(() -> new ApiRequestException("Spending item not found"));
        spendingItemRepository.delete(item);
    }

    private SpendingItemResponse toResponse(SpendingItem item) {
        SpendingItemResponse res = new SpendingItemResponse();
        res.setId(item.getId());
        res.setSpendingPlanId(item.getSpendingPlan().getId());
        res.setItemName(item.getItemName());
        res.setEstimatedCost(item.getEstimatedCost());
        res.setNote(item.getNote());
        res.setCreatedDate(item.getCreatedDate());
        res.setUpdatedDate(item.getUpdatedDate());
        return res;
    }

    public List<SpendingItemResponse> getItemsBySpendingPlan(UUID planId) {
        return spendingItemRepository.findBySpendingPlanId(planId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
