package fptu.fcharity.controller.manage.project;

import fptu.fcharity.dto.project.SubTaskDto;
import fptu.fcharity.dto.project.TaskPlanDto;
import fptu.fcharity.service.manage.project.SubTaskService;
import fptu.fcharity.service.manage.project.TaskPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/projects/task-plans")
@RequiredArgsConstructor
public class TaskPlanController {
    private final TaskPlanService taskPlanService;
    private final SubTaskService subTaskService;

    @GetMapping("/{projectId}")
    public ResponseEntity<?> getTaskPlansOfProject(@PathVariable UUID projectId) {
        return ResponseEntity.ok(taskPlanService.getTaskPlanOfProject(projectId));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<?> getTaskPlanById(@PathVariable UUID taskId) {
        return ResponseEntity.ok(taskPlanService.getTaskPlanById(taskId));
    }

    @GetMapping("/{taskId}/sub-tasks/{subTaskId}")
    public ResponseEntity<?> getSubTaskById(@PathVariable UUID taskId, @PathVariable UUID subTaskId) {
        return ResponseEntity.ok(subTaskService.getSubTaskById(subTaskId));
    }

    @PostMapping("/{taskId}/sub-tasks")
    public ResponseEntity<?> addSubTask(@PathVariable UUID taskId, @RequestBody SubTaskDto subTaskDto) {
        subTaskDto.setTaskPlanId(taskId);
        return ResponseEntity.ok(subTaskService.addSubTask(subTaskDto));
    }

    @PutMapping("/{taskId}/sub-tasks")
    public ResponseEntity<?> updateSubTask(@PathVariable UUID taskId, @RequestBody SubTaskDto subTaskDto) {
        subTaskDto.setTaskPlanId(taskId);
        return ResponseEntity.ok(subTaskService.updateSubTask(subTaskDto));
    }

    @DeleteMapping("/{taskId}/sub-tasks/{subTaskId}")
    public ResponseEntity<?> deleteSubTask(@PathVariable UUID taskId, @PathVariable UUID subTaskId) {
        subTaskService.deleteSubTask(subTaskId);
        return ResponseEntity.ok("Delete subtask successful!");
    }

    @PostMapping
    public ResponseEntity<?> addTaskPlan(@RequestBody TaskPlanDto taskPlanDto) {
        return ResponseEntity.ok(taskPlanService.addTaskPlan(taskPlanDto));
    }

    @PutMapping
    public ResponseEntity<?> updateTaskPlan(@RequestBody TaskPlanDto taskPlanDto) {
        return ResponseEntity.ok(taskPlanService.updateTaskPlan(taskPlanDto));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTaskPlan(@PathVariable UUID taskId) {
        taskPlanService.deleteTaskPlan(taskId);
        return ResponseEntity.ok("Delete successful!");
    }
}