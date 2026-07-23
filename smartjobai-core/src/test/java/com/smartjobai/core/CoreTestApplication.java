package com.smartjobai.core;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Contexto Spring mínimo só para os testes de smartjobai-core. Necessário
 * porque a única classe @SpringBootApplication do projeto vive no módulo
 * api, que core não pode depender (seria circular). O bean de
 * PasswordEncoder também é redeclarado aqui pelo mesmo motivo (só existia
 * em SecurityConfig, no módulo api).
 */
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.smartjobai.core")
@EnableJpaRepositories(basePackages = "com.smartjobai.core.repository")
@EntityScan(basePackages = "com.smartjobai.core.entity")
public class CoreTestApplication {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
