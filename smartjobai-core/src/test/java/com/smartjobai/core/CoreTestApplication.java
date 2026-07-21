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
 * Contexto Spring mínimo só para os testes de {@code smartjobai-core}.
 *
 * Por que isto é necessário: {@code @SpringBootTest} (usado em
 * UsuarioServiceTest, FormacaoServiceTest, etc.) precisa achar uma classe
 * {@code @SpringBootApplication}/{@code @SpringBootConfiguration} subindo a
 * árvore de pacotes a partir do teste. A única classe assim do projeto é
 * {@code SmartJobAIApplication}, no módulo {@code api} — mas {@code core}
 * corretamente NÃO depende de {@code api} (senão seria dependência
 * circular). Sem esta classe aqui, os testes com Testcontainers falhariam
 * já na subida do contexto, antes mesmo de rodar qualquer asserção.
 *
 * Pelo mesmo motivo, o bean de {@link PasswordEncoder} (usado por
 * UsuarioService) só existia em {@code SecurityConfig} no módulo api — por
 * isso ele é redeclarado aqui, só para o escopo de teste.
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
