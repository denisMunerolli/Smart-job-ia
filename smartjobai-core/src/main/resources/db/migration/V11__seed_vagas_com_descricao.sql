-- Remove vagas sem descrição (mock antigo sem dados úteis)
DELETE FROM vagas WHERE descricao IS NULL OR TRIM(descricao) = '';

-- Insere vagas com descrição completa para teste de matching
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

Bachelor degree in Software Engineering, Computer Science or related field preferred.
1-2 years of experience with Java backend development.',
'Brasil (Remoto)', NOW(), NOW(), NOW()),

('seed-002', 'mock', 'Desenvolvedor Java Pleno', 'InovaTI',
'Buscamos Desenvolvedor Java Pleno para atuar em projetos de alta escala.

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

Formação: Graduação em Engenharia de Software, Ciência da Computação ou similar.',
'São Paulo, SP', NOW(), NOW(), NOW()),

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
- JavaScript or TypeScript for frontend understanding

About the role:
You will work alongside senior engineers building financial APIs
used by thousands of users. Production application experience is a plus.

Education: Bachelor in Software Engineering or equivalent experience.
English: Intermediate reading level for technical documentation.',
'Florianópolis, SC (Híbrido)', NOW(), NOW(), NOW()),

('seed-004', 'mock', 'Java Developer - Microservices', 'CloudSoft',
'CloudSoft is growing and needs a Java Developer with microservices experience.

Must Have:
- Java 17 and Spring Boot 3
- Microservices architecture design and implementation
- REST APIs and API Gateway patterns
- Spring Cloud (Eureka, Feign, Config Server)
- PostgreSQL, MySQL or MongoDB
- Docker and Kubernetes basics
- JUnit 5 and Mockito for testing
- Git and CI/CD pipelines

Good to Have:
- Apache Kafka or RabbitMQ
- Distributed tracing (Jaeger, Zipkin)
- Prometheus and Grafana monitoring
- AWS ECS or GCP Cloud Run

We offer:
- 100% remote work
- Flexible schedule
- Home office stipend
- Health insurance

Software Engineering degree preferred. 2+ years Java experience required.',
'Remoto (Brasil)', NOW(), NOW(), NOW()),

('seed-005', 'mock', 'Engenheiro de Software Backend - Java', 'Startup XYZ',
'Startup XYZ busca Engenheiro de Software Backend apaixonado por Java.

Stack principal:
- Java 17 com Spring Boot e Spring Data JPA
- Hibernate e migrations com Flyway
- PostgreSQL como banco principal
- APIs RESTful bem documentadas com Swagger/OpenAPI
- Maven para gerenciamento de dependências
- Git com GitFlow
- Docker para containerização

Diferenciais valorizados:
- Experiência com arquitetura hexagonal ou DDD
- Testes automatizados com JUnit 5 e Mockito
- Conhecimento de Redis para cache
- CI/CD com GitHub Actions ou Jenkins
- Deploy em cloud (AWS, GCP, Railway, Heroku)
- Node.js ou JavaScript como stack secundária

Sobre a vaga:
Você vai construir APIs que atendem aplicações mobile e web.
Trabalhamos com metodologia ágil, code review rigoroso e cultura de aprendizado contínuo.

Requisitos: Graduação em Engenharia de Software, Computação ou áreas correlatas.
Inglês técnico para leitura de documentação.',
'Florianópolis, SC ou Remoto', NOW(), NOW(), NOW())

ON CONFLICT (id_externo) DO NOTHING;
