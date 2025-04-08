package fptu.fcharity.controller.manage.project;

import fptu.fcharity.dto.project.SpendingItemDto;
import fptu.fcharity.dto.project.SpendingPlanDto;
import fptu.fcharity.response.project.SpendingItemResponse;
import fptu.fcharity.response.project.SpendingPlanResponse;
import fptu.fcharity.service.manage.project.SpendingItemService;
import fptu.fcharity.service.manage.project.SpendingPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects/spending")
public class SpendingController {

    @Autowired
    private SpendingItemService spendingItemService;

    @Autowired
    private SpendingPlanService spendingPlanService;

    // ======= SPENDING PLAN CRUD =======

    @PostMapping("/plans")
    public ResponseEntity<?> createPlan(@RequestBody SpendingPlanDto dto) {
        SpendingPlanResponse response = spendingPlanService.createSpendingPlan(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{projectId}/plan")
    public ResponseEntity<?> getPlanByProjectId(@PathVariable UUID projectId) {
        List<SpendingPlanResponse> response = spendingPlanService.getSpendingPlanByProjectId(projectId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/plans/{id}")
    public ResponseEntity<?> getPlanById(@PathVariable UUID id) {
        SpendingPlanResponse response = spendingPlanService.getSpendingPlanById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/plans/{id}")
    public ResponseEntity<?> updatePlan(@PathVariable UUID id, @RequestBody SpendingPlanDto dto) {
        SpendingPlanResponse response = spendingPlanService.updateSpendingPlan(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/plans/{id}")
    public ResponseEntity<?> deletePlan(@PathVariable UUID id) {
        spendingPlanService.deleteSpendingPlan(id);
        return ResponseEntity.noContent().build();
    }

    // ======= SPENDING ITEM CRUD =======

    @PostMapping("/items")
    public ResponseEntity<?> createItem(@RequestBody SpendingItemDto dto) {
        SpendingItemResponse response = spendingItemService.createSpendingItem(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<?> getItemById(@PathVariable UUID id) {
        SpendingItemResponse response = spendingItemService.getSpendingItemById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/items/{id}")
    public ResponseEntity<?> updateItem(@PathVariable UUID id, @RequestBody SpendingItemDto dto) {
        SpendingItemResponse response = spendingItemService.updateSpendingItem(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable UUID id) {
        spendingItemService.deleteSpendingItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/plans/{planId}/items")
    public ResponseEntity<?> getItemsByPlan(@PathVariable UUID planId) {
        List<SpendingItemResponse> response = spendingItemService.getItemsBySpendingPlan(planId);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/plans/{projectId}/{planId}/approve")
    public ResponseEntity<?> approvePlan(@PathVariable UUID planId, @PathVariable UUID projectId) {
        SpendingPlanResponse response = spendingPlanService.approvePlan(planId,projectId);
        return ResponseEntity.ok(response);
    }
}
