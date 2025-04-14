package fptu.fcharity.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class SchedulerConfig {

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(@Qualifier("quartzDataSource") DataSource quartzDataSource) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setDataSource(quartzDataSource);

        Properties quartzProperties = new Properties();
        quartzProperties.setProperty("org.quartz.jobStore.tablePrefix", "QRTZ_");
        quartzProperties.setProperty("org.quartz.jobStore.isClustered", "true");
        // Thêm các cấu hình jobStore khác nếu cần
        factory.setQuartzProperties(quartzProperties);

        return factory;
    }
}
