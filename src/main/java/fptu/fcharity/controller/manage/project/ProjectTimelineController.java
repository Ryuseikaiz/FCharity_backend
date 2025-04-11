package fptu.fcharity.controller.manage.project;

import fptu.fcharity.dto.project.TaskPlanDto;
import fptu.fcharity.dto.project.TaskPlanStatusDto;
import fptu.fcharity.dto.project.TimelineDto;
import fptu.fcharity.entity.TaskPlan;
import fptu.fcharity.entity.TaskPlanStatus;
import fptu.fcharity.entity.Timeline;
import fptu.fcharity.response.project.TaskPlanResponse;
import fptu.fcharity.service.manage.project.TaskPlanService;
import fptu.fcharity.service.manage.project.TaskPlanStatusService;
import fptu.fcharity.service.manage.project.TimelineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects/timeline")
public class ProjectTimelineController {
    @Autowired
    TimelineService timelineService;
    @Autowired
    TaskPlanService taskPlanService;
    @Autowired
    TaskPlanStatusService taskPlanStatusService;
    //get by id
    @GetMapping("/by-projectId/{project_id}")
    public ResponseEntity<?> getAllPhaseByProject(@PathVariable UUID project_id) {
        List<Timeline> t = timelineService.getPhaseByProjectId(project_id);
        return ResponseEntity.ok(t);
    }

    //get by id
    @GetMapping("/by-id/{phase_id}")
    public ResponseEntity<?> getPhase(@PathVariable UUID phase_id) {
        Timeline t = timelineService.getPhaseById(phase_id);
        return ResponseEntity.ok(t);
    }

    //tạo phase: cần tên--okay
    @PostMapping("/create")
    public ResponseEntity<?> createPhase(@RequestBody TimelineDto tDto) {
        Timeline t = timelineService.addPhase(tDto);
        return ResponseEntity.ok(t);
    }
    //update phase: chỉ sửa tên thôi
    @PostMapping("/update")
    public ResponseEntity<?> updatePhase(@RequestBody TimelineDto tDto) {
        Timeline t = timelineService.updatePhase(tDto);
        return ResponseEntity.ok(t);
    }
    //end phase: chỉ sửa content thôi
    @PostMapping("/end")
    public ResponseEntity<?> endPhase(@RequestBody TimelineDto tDto) {
        tDto.setEndTime(Instant.now());
        Timeline t = timelineService.updatePhase(tDto);
        return ResponseEntity.ok(t);
    }
    //delete phase
    @PostMapping("/{phaseId}/cancel")
    public ResponseEntity<?> cancelPhase(@PathVariable UUID phaseId) {
        boolean t = timelineService.deletePhase(phaseId);
        return ResponseEntity.ok(t);
    }
//*******************TASK PLAN***********************************
    //tạo task
    //update task
    //delete task
    //get task of phase
    //get subtask of task--okay
    @GetMapping("/{phaseId}/tasks")
    public ResponseEntity<?> getTasksOfPhase(@PathVariable UUID phaseId) {
        List<TaskPlanResponse> t = timelineService.getTasksOfPhase(phaseId);
        return ResponseEntity.ok(t);
    }
    //--okay
    @GetMapping("/{taskId}/subtasks")
    public ResponseEntity<?> getSubtasksOfTask(@PathVariable UUID taskId) {
        List<TaskPlanResponse> t = timelineService.getSubtasksOfTask(taskId);
        return ResponseEntity.ok(t);
    }
    //--okay
    @PostMapping("/{phaseId}/create")
    public ResponseEntity<?> addTaskOfPhase(@PathVariable UUID phaseId, @RequestBody TaskPlanDto tDto) {
        tDto.setPhaseId(phaseId);
       TaskPlanResponse t = taskPlanService.addTask(tDto);
        return ResponseEntity.ok(t);
    }
    //--okay
    @PostMapping("/{taskId}/update")
    public ResponseEntity<?> updateTaskOfPhase(@PathVariable UUID taskId,@RequestBody TaskPlanDto tDto) {
        tDto.setId(taskId);
        TaskPlanResponse t = taskPlanService.updateTask(tDto);
        return ResponseEntity.ok(t);
    }
    @PostMapping("/{taskId}/cancel-task")
    public ResponseEntity<?> cancelTaskOfPhase(@PathVariable UUID taskId) {
        boolean res = taskPlanService.cancelTask(taskId);
        return ResponseEntity.ok(res);
    }

    //*****************TASK PLAN STATUS **************************
    //get all status--okay
    @GetMapping("/task-status")
    public ResponseEntity<?> getAllTaskPlanStatus() {
        List<TaskPlanStatus> t = taskPlanStatusService.getAllStatus();
        return ResponseEntity.ok(t);
    }
    //add status
    //update status
    //delete status
    // Add status
    @PostMapping("/task-status/add")
    public ResponseEntity<?> addTaskStatus(@RequestBody TaskPlanStatusDto tDto) {
        TaskPlanStatus t = taskPlanStatusService.addTaskStatus(tDto);
        return ResponseEntity.ok(t);
    }

    // Update status--okay
    @PostMapping("/task-status/update")
    public ResponseEntity<?> updateTaskStatus(@RequestBody TaskPlanStatusDto tDto) {
        TaskPlanStatus t = taskPlanStatusService.updateTaskStatus(tDto);
        return ResponseEntity.ok(t);
    }

    // Delete status--okay
    @DeleteMapping("/{statusId}/delete")
    public ResponseEntity<?> deleteTaskStatus(@PathVariable UUID statusId) {
        boolean result = taskPlanStatusService.deleteTaskStatus(statusId);
        return ResponseEntity.ok(result);
    }

}
