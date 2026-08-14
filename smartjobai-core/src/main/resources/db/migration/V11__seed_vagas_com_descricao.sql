-- Remove vagas sem descricao (limpeza do mock antigo)
DELETE FROM vagas WHERE (descricao IS NULL OR TRIM(CAST(descricao AS TEXT)) = '');

-- Remove seeds anteriores para evitar duplicatas em rerun
DELETE FROM vagas WHERE id_externo IN ('seed-001','seed-002','seed-003','seed-004','seed-005');

-- Insere 5 vagas com descricao completa para testes de matching
INSERT INTO vagas (id_externo, fonte, titulo, empresa, descricao, localizacao, data_coleta, created_at, updated_at)
SELECT * FROM (VALUES
  ('seed-001'::varchar, 'mock'::varchar, 'Junior Java Backend Developer'::varchar, 'TechCorp'::varchar,
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
- Docker basics

Preferred:
- Microservices architecture experience
- JUnit and Mockito for unit testing
- Cloud deployment: AWS, Railway, Heroku
- SOLID principles

Benefits: Remote work, competitive salary, learning budget.
Bachelor degree in Software Engineering preferred. 1-2 years Java experience.'::text,
   'Brasil (Remoto)'::varchar, NOW()::timestamp, NOW()::timestamp, NOW()::timestamp),

  ('seed-002', 'mock', 'Desenvolvedor Java Pleno', 'InovaTI',
   'Buscamos Desenvolvedor Java Pleno para projetos de alta escala.

Requisitos:
- Java 17 com Spring Boot 3.x
- Spring Data JPA e Hibernate
- APIs REST e integracao com sistemas externos
- PostgreSQL e SQL avancado
- Git, Scrum, Kanban
- Docker
- JUnit 5 e Mockito

Desejaveis:
- Microservicos, Kafka, RabbitMQ
- Kubernetes e CI/CD
- Redis para cache

Beneficios: VR, VA, plano de saude, home office 3x.
Graduacao em Engenharia de Software ou Ciencia da Computacao.',
   'Sao Paulo, SP', NOW(), NOW(), NOW()),

  ('seed-003', 'mock', 'Backend Developer Spring Boot', 'FinTech Brasil',
   'FinTech Brasil is hiring a Backend Developer specialized in Spring Boot.

Technical Requirements:
- Java 17: OOP, Collections, Generics, Exception Handling
- Spring Boot, Spring Security, Spring Data JPA
- RESTful API best practices
- Hibernate ORM and Flyway migrations
- PostgreSQL and SQL optimization
- Maven or Gradle
- Git and code review
- Docker

Nice to have:
- Kafka messaging
- AWS or Azure cloud
- Performance monitoring
- JavaScript or TypeScript

Education: Bachelor in Software Engineering. English: Intermediate.',
   'Florianopolis, SC Hibrido', NOW(), NOW(), NOW()),

  ('seed-004', 'mock', 'Java Developer Microservices', 'CloudSoft',
   'CloudSoft needs a Java Developer with microservices experience.

Must Have:
- Java 17 and Spring Boot 3
- Microservices architecture
- REST APIs and API Gateway
- Spring Cloud: Eureka, Feign
- PostgreSQL or MySQL
- Docker and Kubernetes basics
- JUnit 5 and Mockito
- Git and CI/CD

Good to Have:
- Kafka or RabbitMQ
- Prometheus and Grafana
- AWS ECS or GCP Cloud Run

Benefits: 100% remote, flexible schedule, health insurance.
2 years Java experience required.',
   'Remoto Brasil', NOW(), NOW(), NOW()),

  ('seed-005', 'mock', 'Engenheiro de Software Backend Java', 'Startup XYZ',
   'Startup XYZ busca Engenheiro de Software Backend com foco em Java.

Stack:
- Java 17 com Spring Boot e Spring Data JPA
- Hibernate e Flyway migrations
- PostgreSQL
- APIs RESTful com Swagger e OpenAPI
- Maven, Git, Docker

Diferenciais:
- JUnit 5 e Mockito
- Redis para cache
- CI/CD com GitHub Actions
- Deploy em cloud: AWS, GCP, Railway, Heroku
- Node.js ou JavaScript

Voce vai construir APIs para aplicacoes mobile e web.
Metodologia agil, code review e aprendizado continuo.

Requisitos: Graduacao em Engenharia de Software.
Ingles tecnico.',
   'Florianopolis SC ou Remoto', NOW(), NOW(), NOW())
) AS t(id_externo, fonte, titulo, empresa, descricao, localizacao, data_coleta, created_at, updated_at);
