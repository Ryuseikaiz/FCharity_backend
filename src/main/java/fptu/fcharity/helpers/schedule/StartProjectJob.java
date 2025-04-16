package fptu.fcharity.helpers.schedule;

import fptu.fcharity.service.manage.project.ProjectService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StartProjectJob implements Job {

    @Autowired
    private ProjectService projectService;
    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap dataMap = context.getMergedJobDataMap();
        UUID projectId = UUID.fromString(dataMap.getString("projectId"));

        System.out.println("Running StartProjectJob for project: " + projectId);
        projectService.handleCreateProjectJob(projectId);
    }
}

