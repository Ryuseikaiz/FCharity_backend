package fptu.fcharity.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class QuartzConfig {

    @Primary
    @Bean(name = "quartzDataSource")
    @ConfigurationProperties(prefix = "spring.quartz.datasource")
    public DataSource quartzDataSource() {
        return DataSourceBuilder.create().build();
    }
}

