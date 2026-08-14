-- Remove vagas sem descricao (mock antigo sem dados uteis)
DELETE FROM vagas WHERE descricao IS NULL OR TRIM(descricao) = '';

-- Remove seeds anteriores se existirem para evitar duplicatas
DELETE FROM vagas WHERE id_externo IN ('seed-001','seed-002','seed-003','seed-004','seed-005');

-- Insere vagas com descricao completa
INSERT INTO vagas (id_externo, fonte, titulo, empresa, descricao, localizacao, data_coleta, created_at, updated_at)
VALUES

('seed-001', 'mock', 'Junior Java Backend Developer', 'TechCorp',
'We are looking for a Junior Java Backend Developer to join our engineering team.

Requirements:
- Java 11+ and Spring Boot development experience
- REST APIs design and implementation
- Spring Data JPA and Hibernate for database persistence
- PostgreSQL or MySQL relational databases
- Git version control
- Object-Oriented Programming: interfaces, inheritance, generics
- Exception handling and debugging skills
- Maven for build management
- Docker basics for containerization

Preferred Qualifications:
- Microservices architecture experience
- JUnit and Mockito for unit testing
- Cloud deployment experience (AWS, Railway, Heroku)
- SOLID principles understanding

Benefits:
- Remote work flexibility
- Competitive salary
- Learning and development budget

Bachelor degree in Software Engineering or Computer Science preferred.
1-2 years of experience with Java backend development.',
'Brasil (Remoto)', NOW(), NOW(), NOW()),

('seed-002', 'mock', 'Desenvolvedor Java Pleno', 'InovaTI',
'Buscamos Desenvolvedor Java Pleno para atuar em projetos de alta escala.

Requisitos obrigatorios:
- Java 17 com Spring Boot 3.x
- Spring Data JPA e Hibernate
- APIs REST e integracao com sistemas externos
- Banco de dados PostgreSQL e SQL avancado
- Git e metodologias ageis Scrum e Kanban
- Docker e ambientes containerizados
- Testes unitarios com JUnit 5 e Mockito

Requisitos desejaveis:
- Experiencia com microservicos e mensageria Kafka e RabbitMQ
- Kubernetes e CI/CD
- Redis para cache
- Clean Code e Design Patterns

Beneficios:
- Vale refeicao e alimentacao
- Plano de saude e odontologico
- Home office 3x por semana

Formacao: Graduacao em Engenharia de Software, Ciencia da Computacao ou similar.',
'Sao Paulo, SP', NOW(), NOW(), NOW()),

('seed-003', 'mock', 'Backend Developer - Spring Boot', 'FinTech Brasil',
'FinTech Brasil is hiring a Backend Developer specialized in Spring Boot.

Technical Requirements:
- Strong Java 17 knowledge: OOP, Collections, Generics, Exception Handling
- Spring Boot, Spring Security, Spring Data JPA
- RESTful API development and best practices
- Hibernate ORM and database migrations with Flyway
- PostgreSQL and SQL query optimization
- Maven or Gradle build tools
- Git workflow and code review practices
- Docker for local development and deployment

Nice to have:
- Event-driven architecture with Kafka
- AWS or Azure cloud services
- Performance testing and monitoring
- JavaScript or TypeScript knowledge

About the role:
You will work alongside senior engineers building financial APIs
used by thousands of users. Production application experience is a plus.

Education: Bachelor in Software Engineering or equivalent experience.
English: Intermediate reading level for technical documentation.',
'Florianopolis, SC (Hibrido)', NOW(), NOW(), NOW()),

('seed-004', 'mock', 'Java Developer - Microservices', 'CloudSoft',
'CloudSoft is growing and needs a Java Developer with microservices experience.

Must Have:
- Java 17 and Spring Boot 3
- Microservices architecture design and implementation
- REST APIs and API Gateway patterns
- Spring Cloud with Eureka and Feign
- PostgreSQL or MySQL databases
- Docker and Kubernetes basics
- JUnit 5 and Mockito for testing
- Git and CI/CD pipelines

Good to Have:
- Apache Kafka or RabbitMQ messaging
- Distributed tracing with Jaeger or Zipkin
- Prometheus and Grafana monitoring
- AWS ECS or GCP Cloud Run

Benefits: 100% remote, flexible schedule, health insurance.

Software Engineering degree preferred. 2 years Java experience required.',
'Remoto (Brasil)', NOW(), NOW(), NOW()),

('seed-005', 'mock', 'Engenheiro de Software Backend - Java', 'Startup XYZ',
'Startup XYZ busca Engenheiro de Software Backend com foco em Java.

Stack principal:
- Java 17 com Spring Boot e Spring Data JPA
- Hibernate e migrations com Flyway
- PostgreSQL como banco principal
- APIs RESTful documentadas com Swagger e OpenAPI
- Maven para gerenciamento de dependencias
- Git com GitFlow
- Docker para containerizacao

Diferenciais valorizados:
- Testes automatizados com JUnit 5 e Mockito
- Conhecimento de Redis para cache
- CI/CD com GitHub Actions ou Jenkins
- Deploy em cloud AWS, GCP, Railway ou Heroku
- Node.js ou JavaScript como stack secundaria

Sobre a vaga:
Voce vai construir APIs para aplicacoes mobile e web.
Trabalhamos com metodologia agil, code review e cultura de aprendizado continuo.

Requisitos: Graduacao em Engenharia de Software ou areas correlatas.
Ingles tecnico para leitura de documentacao.',
'Florianopolis, SC ou Remoto', NOW(), NOW(), NOW());
