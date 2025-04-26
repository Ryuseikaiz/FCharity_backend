package fptu.fcharity.controller.manage.project;

import fptu.fcharity.dto.project.SpendingDetailDto;
import fptu.fcharity.dto.project.SpendingItemDto;
import fptu.fcharity.dto.project.SpendingPlanDto;
import fptu.fcharity.entity.Project;
import fptu.fcharity.entity.SpendingDetail;
import fptu.fcharity.entity.SpendingItem;
import fptu.fcharity.entity.SpendingPlan;
import fptu.fcharity.helpers.schedule.ScheduleService;
import fptu.fcharity.repository.manage.project.ProjectRepository;
import fptu.fcharity.response.project.SpendingItemResponse;
import fptu.fcharity.response.project.SpendingPlanReaderResponse;
import fptu.fcharity.response.project.SpendingPlanResponse;
import fptu.fcharity.service.manage.project.*;
import fptu.fcharity.utils.constants.project.SpendingDetailResponse;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/projects/spending")
public class SpendingController {

    @Autowired
    private SpendingItemService spendingItemService;

    @Autowired
    private SpendingPlanService spendingPlanService;
    @Autowired
    private ExcelService excelService;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private SpendingDetailService spendingDetailService;

    // ======= SPENDING PLAN CRUD =======
    @GetMapping("/{projectId}/download-template")
    public ResponseEntity<Resource> downloadTemplate(@PathVariable UUID projectId) throws IOException {
        ByteArrayInputStream in = excelService.generateSpendingPlanTemplate( projectId);
        InputStreamResource file = new InputStreamResource(in);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=spending_template.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }
    @GetMapping("/{projectId}/download-template-expense")
    public ResponseEntity<Resource> downloadTemplateExpense(@PathVariable UUID projectId) throws IOException {
        ByteArrayInputStream in = excelService.generateSpendingDetailTemplate( projectId);
        InputStreamResource file = new InputStreamResource(in);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=expense_template.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

    @PostMapping("/{projectId}/save-items-from-template")
    public ResponseEntity<?> saveSpendingItems(@RequestParam MultipartFile file,@PathVariable UUID projectId) throws IOException {
       SpendingPlanReaderResponse l = excelService.parseNewSpendingPlanExcel(file);
       if(!l.getErrors().isEmpty()){
              return ResponseEntity.badRequest().body(l.getErrors());
       }
       SpendingPlanResponse p = spendingPlanService.saveFromExcel(l.getSpendingPlan(),projectId);
       List<SpendingItemResponse> res =  spendingItemService.saveFromExcel(l.getSpendingItems(),p.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("plan", p);
        response.put("items", res);
       return ResponseEntity.ok(response);
    }
    @PostMapping("/{projectId}/save-expenses-from-template")
    public ResponseEntity<?> saveSpendingDetails(@RequestParam MultipartFile file,@PathVariable UUID projectId) throws IOException {
        spendingDetailService.removeNonExtraFundSpendingDetails(projectId);
        List<SpendingDetail> l = excelService.importSpendingDetails(projectId,file);
        List<SpendingDetailResponse> responseList =  spendingDetailService.saveFromExcel(l);
        List<SpendingDetailResponse> response = spendingDetailService.getSpendingDetailsByProject(projectId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/plans")
    public ResponseEntity<?> createPlan(@RequestBody SpendingPlanDto dto) {
        SpendingPlanResponse response = spendingPlanService.createSpendingPlan(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{projectId}/plan")
    public ResponseEntity<?> getPlanByProjectId(@PathVariable UUID projectId) {
        SpendingPlanResponse response = spendingPlanService.getSpendingPlanByProjectId(projectId);
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
    @PostMapping("/plans/{planId}/approve")
    public ResponseEntity<?> approvePlan(@PathVariable UUID planId) {
        SpendingPlanResponse response = spendingPlanService.approvePlan(planId);
        Project project = projectRepository.findWithEssentialById(response.getProjectId());
        scheduleService.handleSetJob(project.getId(),project.getPlannedStartTime());
        return ResponseEntity.ok(response);
    }
    @PostMapping("/plans/{planId}/reject")
    public ResponseEntity<?> rejectPlan(@PathVariable UUID planId,@RequestParam String reason) {
        SpendingPlanResponse response = spendingPlanService.rejectPlan(planId,reason);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{projectId}/details")
    public ResponseEntity<?> getSpendingDetails(@PathVariable UUID projectId) {
        List<SpendingDetailResponse> response = spendingDetailService.getSpendingDetailsByProject(projectId);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/details/create")
    public ResponseEntity<?> createSpendingDetail(@RequestBody SpendingDetailDto dto) {
        SpendingDetailResponse response = spendingDetailService.createSpendingDetail(dto);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/details/{id}")
    public ResponseEntity<?> getSpendingDetailById(@PathVariable UUID id) {
        SpendingDetailResponse response = spendingDetailService.getSpendingDetailById(id);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/details/{id}")
    public ResponseEntity<?> updateSpendingDetail(@PathVariable UUID id, @RequestBody SpendingDetailDto dto) {
        SpendingDetailResponse response = spendingDetailService.updateSpendingDetail(id, dto);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/details/{id}")
    public ResponseEntity<?> deleteSpendingDetail(@PathVariable UUID id) {
        spendingDetailService.deleteSpendingDetail(id);
        return ResponseEntity.noContent().build();
    }
}
