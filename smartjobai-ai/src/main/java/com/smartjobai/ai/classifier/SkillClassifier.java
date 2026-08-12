package com.smartjobai.ai.classifier;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Classifica termos em hard skills, condições de trabalho ou geral.
 * Usa HashSet inicializado em método estático para evitar ExceptionInInitializerError
 * que ocorre com Set.of() quando há elementos duplicados acidentais.
 */
@Component
public class SkillClassifier {

    public enum TermCategory {
        HARD_SKILL,
        SOFT_SKILL,
        JOB_CONDITION,
        GENERAL
    }

    private static final Set<String> JOB_CONDITIONS = buildJobConditions();
    private static final Set<String> HARD_SKILLS    = buildHardSkills();

    private static Set<String> buildJobConditions() {
        Set<String> s = new HashSet<>(Arrays.asList(
            "remote", "hybrid", "flexible", "home", "office", "onsite",
            "schedule", "benefits", "salary", "compensation", "equity", "bonus",
            "work", "working", "environment", "culture", "team", "opportunity",
            "location", "relocation", "travel", "timezone", "async",
            "fulltime", "parttime", "contract", "freelance", "permanent",
            "temporary", "internship", "junior", "senior", "mid", "lead",
            "year", "years", "month", "months", "experience", "background",
            "required", "preferred", "plus", "ideal", "nice",
            "understanding", "knowledge", "familiarity", "exposure", "ability",
            "strong", "good", "excellent", "proficient", "proven",
            "passion", "motivated", "collaborative", "communication",
            "problem", "solving", "analytical", "detail", "oriented",
            "independent", "self", "starter", "fast", "learner", "eager",
            "degree", "bachelor", "master", "phd", "university", "college",
            "english", "portuguese", "spanish", "french", "language",
            "join", "help", "build", "create", "develop", "maintain", "support",
            "skills", "skill", "technologies", "technology", "tools",
            "looking", "seeking", "candidate", "applicant", "position", "role",
            "company", "startup", "enterprise", "global", "international",
            "medical", "dental", "vision", "insurance", "pto", "vacation",
            "stock", "options", "growth", "learning", "development", "career",
            "hands", "proven", "ability", "written", "verbal"
        ));
        return s;
    }

    private static Set<String> buildHardSkills() {
        Set<String> s = new HashSet<>(Arrays.asList(
            // Linguagens
            "java", "python", "javascript", "typescript", "kotlin", "scala",
            "go", "golang", "rust", "ruby", "php", "swift", "sql", "html", "css",
            // Frameworks Java
            "spring", "springboot", "hibernate", "jpa", "spring-boot",
            "spring-data", "spring-security", "spring-mvc", "spring-cloud",
            "quarkus", "micronaut", "jakarta",
            // Frontend
            "react", "angular", "vue", "nextjs", "nodejs", "express",
            "tailwind", "bootstrap", "redux", "graphql", "webpack", "vite",
            // Bancos
            "postgresql", "mysql", "oracle", "mongodb", "redis",
            "elasticsearch", "cassandra", "dynamodb", "sqlite", "mariadb",
            "h2", "flyway", "liquibase",
            // Cloud / DevOps
            "aws", "azure", "gcp", "docker", "kubernetes", "terraform",
            "jenkins", "ansible", "nginx", "linux", "railway", "heroku",
            "vercel", "netlify",
            // Arquitetura
            "microservices", "rest", "restful", "api", "soap", "grpc",
            "kafka", "rabbitmq", "cqrs", "ddd", "tdd", "bdd", "solid", "oop",
            // Testes
            "junit", "mockito", "testcontainers", "jest", "pytest",
            "selenium", "cypress", "cucumber",
            // Ferramentas
            "git", "maven", "gradle", "npm", "yarn", "postman", "jira",
            // IA/ML
            "tensorflow", "pytorch", "pandas", "numpy",
            // Mobile
            "android", "ios", "flutter"
        ));
        return s;
    }

    public TermCategory classify(String term) {
        String t = term.toLowerCase().trim();
        if (JOB_CONDITIONS.contains(t)) return TermCategory.JOB_CONDITION;
        if (HARD_SKILLS.contains(t))    return TermCategory.HARD_SKILL;
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
