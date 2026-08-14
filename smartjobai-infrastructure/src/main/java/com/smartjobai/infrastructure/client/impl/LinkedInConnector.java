package com.smartjobai.infrastructure.client.impl;

import com.smartjobai.core.entity.Vaga;
import com.smartjobai.infrastructure.client.VagaConnector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Conector mock para desenvolvimento.
 * Retorna vagas realistas com descrição completa para teste do matching.
 * Em produção, configure ADZUNA_APP_ID para usar vagas reais.
 */
@Component
@Slf4j
public class LinkedInConnector implements VagaConnector {

    private static final String FONTE = "mock";

    private static final List<Map<String, String>> VAGAS_MOCK = List.of(
        Map.of(
            "titulo", "Junior Java Backend Developer",
            "empresa", "TechCorp",
            "localizacao", "Brasil (Remoto)",
            "descricao", """
                We are looking for a Junior Java Backend Developer to join our engineering team.
                
                Requirements:
                - Java 11+ and Spring Boot development experience
                - REST APIs design and implementation
                - Spring Data JPA and Hibernate for database persistence
                - PostgreSQL or MySQL relational databases
                - Git version control
                - Object-Oriented Programming principles (interfaces, inheritance, generics)
                - Exception handling and debugging skills
                - Maven for build management
                - Docker basics for containerization
                
                Preferred Qualifications:
                - Microservices architecture experience
                - JUnit and Mockito for unit testing
                - Cloud deployment experience (AWS, Railway, Heroku)
                - Understanding of SOLID principles
                
                What we offer:
                - Remote work flexibility
                - Competitive salary
                - Learning and development budget
                - Collaborative team environment
                
                Bachelor degree in Software Engineering, Computer Science or related field preferred.
                1-2 years of experience with Java backend development.
                """
        ),
        Map.of(
            "titulo", "Desenvolvedor Java Pleno",
            "empresa", "InovaTI",
            "localizacao", "São Paulo, SP",
            "descricao", """
                Buscamos Desenvolvedor Java Pleno para atuar em projetos de alta escala.
                
                Requisitos obrigatórios:
                - Java 17 com Spring Boot 3.x
                - Spring Data JPA e Hibernate
                - APIs REST e integração com sistemas externos
                - Banco de dados PostgreSQL e SQL avançado
                - Git e metodologias ágeis (Scrum/Kanban)
                - Docker e ambientes containerizados
                - Testes unitários com JUnit 5 e Mockito
                
                Requisitos desejáveis:
                - Experiência com microserviços e mensageria (Kafka, RabbitMQ)
                - Kubernetes e CI/CD
                - Redis para cache
                - Clean Code e Design Patterns
                
                Benefícios:
                - Vale refeição e alimentação
                - Plano de saúde e odontológico
                - Home office 3x por semana
                - Participação nos lucros
                
                Formação: Graduação em Engenharia de Software, Ciência da Computação ou similar.
                """
        ),
        Map.of(
            "titulo", "Backend Developer - Spring Boot",
            "empresa", "FinTech Brasil",
            "localizacao", "Florianópolis, SC (Híbrido)",
            "descricao", """
                FinTech Brasil is hiring a Backend Developer specialized in Spring Boot.
                
                Technical Requirements:
                - Strong Java 17 knowledge — OOP, Collections, Generics, Exception Handling
                - Spring Boot, Spring Security, Spring Data JPA
                - RESTful API development and best practices
                - Hibernate ORM and database migrations with Flyway
                - PostgreSQL and SQL query optimization
                - Maven or Gradle build tools
                - Git workflow (branches, pull requests, code review)
                - Docker for local development
                
                Nice to have:
                - Event-driven architecture with Kafka
                - AWS or Azure cloud services
                - Performance testing and monitoring
                - JavaScript/TypeScript for frontend understanding
                
                About the role:
                You will work alongside senior engineers building financial APIs
                used by thousands of users. Production application experience is a plus.
                
                Education: Bachelor in Software Engineering or equivalent experience.
                English: Intermediate (reading technical documentation).
                """
        )
    );

    @Override
    public List<Vaga> buscarVagas(String termo, String localizacao) {
        log.info("[MOCK] Buscando vagas no LinkedIn para: {} em {}", termo, localizacao);
        return VAGAS_MOCK.stream()
                .filter(v -> v.get("titulo").toLowerCase().contains(termo.toLowerCase())
                          || v.get("descricao").toLowerCase().contains(termo.toLowerCase()))
                .map(v -> Vaga.builder()
                        .idExterno("mock-" + v.get("titulo").hashCode())
                        .fonte(FONTE)
                        .titulo(v.get("titulo"))
                        .empresa(v.get("empresa"))
                        .localizacao(v.get("localizacao"))
                        .descricao(v.get("descricao").strip())
                        .dataColeta(LocalDateTime.now())
                        .build())
                .toList();
    }

    @Override
    public Vaga detalharVaga(String idExterno) {
        return null;
    }

    @Override
    public void candidatar(Vaga vaga, String curriculoJson) {
        log.info("[MOCK] Candidatura registrada para '{}'", vaga.getTitulo());
    }
}
