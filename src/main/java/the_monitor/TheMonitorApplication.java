package the_monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories(basePackages = "the_monitor.domain.repository")  // JPA 저장소 위치 지정
@EnableJdbcRepositories(basePackages = "the_monitor.jdbc.repository")   // JDBC 저장소 위치 지정
@EnableJpaAuditing
public class TheMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(TheMonitorApplication.class, args);
    }

}