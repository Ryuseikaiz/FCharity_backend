package fptu.fcharity.response.project;

import fptu.fcharity.entity.SubTask;
import fptu.fcharity.entity.TaskPlan;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class TaskPlanFinalResponse {
    private TaskPlan taskPlan;
    private List<SubTask> subTasks;
}
