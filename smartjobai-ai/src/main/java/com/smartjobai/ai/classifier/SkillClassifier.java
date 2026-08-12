package com.smartjobai.ai.classifier;

import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Classifica termos extraídos de vagas e currículos em categorias.
 * Isso evita que palavras de contexto (remote, work, home) sejam
 * tratadas como habilidades técnicas pelo TF-IDF.
 */
@Component
public class SkillClassifier {

    /** Palavras de contexto/condições de trabalho — não são skills */
    private static final Set<String> JOB_CONDITIONS = Set.of(
        "remote", "hybrid", "flexible", "home", "office", "onsite", "on-site",
        "schedule", "benefits", "salary", "compensation", "equity", "bonus",
        "work", "working", "environment", "culture", "team", "opportunity",
        "location", "relocation", "travel", "timezone", "async", "asynchronous",
        "fulltime", "full-time", "parttime", "part-time", "contract", "freelance",
        "permanent", "temporary", "internship", "junior", "senior", "mid", "lead",
        "year", "years", "month", "months", "experience", "background",
        "required", "preferred", "plus", "bonus", "ideal", "nice",
        "understanding", "knowledge", "familiarity", "exposure", "ability",
        "strong", "good", "excellent", "proficient", "hands-on", "proven",
        "passion", "motivated", "collaborative", "communication", "interpersonal",
        "problem", "solving", "analytical", "detail", "oriented", "organized",
        "independent", "self", "starter", "fast", "learner", "eager",
        "degree", "bachelor", "master", "phd", "university", "college",
        "english", "portuguese", "spanish", "french", "language",
        "join", "help", "build", "create", "develop", "maintain", "support",
        "ability", "skills", "skill", "technologies", "technology", "tools",
        "looking", "seeking", "candidate", "applicant", "position", "role",
        "company", "startup", "enterprise", "global", "international",
        "medical", "dental", "vision", "insurance", "pto", "vacation",
        "stock", "options", "growth", "learning", "development", "career"
    );

    /** Hard skills técnicas conhecidas — têm alto peso no matching */
    private static final Set<String> HARD_SKILLS = Set.of(
        // Linguagens
        "java", "python", "javascript", "typescript", "kotlin", "scala",
        "go", "golang", "rust", "c", "c++", "c#", "ruby", "php", "swift",
        "r", "matlab", "sql", "html", "css", "bash", "shell",
        // Frameworks Java
        "spring", "springboot", "spring-boot", "hibernate", "jpa",
        "spring-data", "spring-security", "spring-mvc", "spring-cloud",
        "quarkus", "micronaut", "jakarta", "javaee",
        // Frontend
        "react", "angular", "vue", "nextjs", "nodejs", "express",
        "tailwind", "bootstrap", "redux", "graphql", "webpack", "vite",
        // Bancos
        "postgresql", "mysql", "oracle", "sqlserver", "mongodb",
        "redis", "elasticsearch", "cassandra", "dynamodb", "sqlite",
        "mariadb", "h2", "flyway", "liquibase",
        // Cloud / DevOps
        "aws", "azure", "gcp", "docker", "kubernetes", "terraform",
        "jenkins", "github-actions", "gitlab-ci", "ansible", "nginx",
        "linux", "unix", "railway", "heroku", "vercel", "netlify",
        // Arquitetura
        "microservices", "rest", "restful", "api", "soap", "grpc",
        "kafka", "rabbitmq", "activemq", "event-driven", "cqrs",
        "ddd", "tdd", "bdd", "solid", "oop", "functional",
        // Testes
        "junit", "mockito", "testcontainers", "jest", "pytest",
        "selenium", "cypress", "cucumber",
        // Ferramentas
        "git", "maven", "gradle", "npm", "yarn", "intellij", "eclipse",
        "vscode", "postman", "jira", "confluence", "sonar",
        // IA/ML
        "tensorflow", "pytorch", "scikit-learn", "pandas", "numpy",
        "machine-learning", "deep-learning", "nlp", "tfidf",
        // Mobile
        "android", "ios", "flutter", "react-native", "xamarin"
    );

    public enum TermCategory {
        HARD_SKILL,     // Tecnologia, linguagem, framework — peso alto
        SOFT_SKILL,     // Habilidade comportamental — peso médio
        JOB_CONDITION,  // Condição de trabalho — ignorar no matching técnico
        GENERAL         // Outros — peso baixo
    }

    public TermCategory classify(String term) {
        String t = term.toLowerCase().trim();
        if (JOB_CONDITIONS.contains(t)) return TermCategory.JOB_CONDITION;
        if (HARD_SKILLS.contains(t))    return TermCategory.HARD_SKILL;
        // Soft skills geralmente são palavras únicas e abstratas
        if (t.length() <= 3)            return TermCategory.GENERAL;
        return TermCategory.GENERAL;
    }

    public boolean isJobCondition(String term) {
        return JOB_CONDITIONS.contains(term.toLowerCase().trim());
    }

    public boolean isHardSkill(String term) {
        return HARD_SKILLS.contains(term.toLowerCase().trim());
    }
}
