package com.smartjobai.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.smartjobai")
@EnableJpaRepositories(basePackages = "com.smartjobai.core.repository")
@EntityScan(basePackages = "com.smartjobai.core.entity")
@EnableScheduling
public class SmartJobAIApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartJobAIApplication.class, args);
    }
}
