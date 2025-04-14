package fptu.fcharity.helpers.schedule;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class ScheduleService {

        @Autowired
        private Scheduler scheduler;

        public void handleSetJob(UUID projectId, Instant plannedStartTime) {
            try {
                JobDetail jobDetail = JobBuilder.newJob(StartProjectJob.class)
                        .withIdentity("startProjectJob_" + projectId, "project-jobs")
                        .usingJobData("projectId", projectId.toString())
                        .build();

                Trigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity("startProjectTrigger_" + projectId, "project-triggers")
                        .startAt(Date.from(plannedStartTime))
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                        .build();

                scheduler.scheduleJob(jobDetail, trigger);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }

}
