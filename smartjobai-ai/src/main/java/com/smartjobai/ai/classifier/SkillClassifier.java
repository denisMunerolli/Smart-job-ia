package com.smartjobai.ai.classifier;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Classifica termos extraídos de vagas e currículos em categorias semânticas.
 *
 * Categorias:
 * - HARD_SKILL      → tecnologia, linguagem, framework (peso alto)
 * - REQUIRED_SKILL  → skill marcada como obrigatória na vaga
 * - PREFERRED_SKILL → skill marcada como preferida/desejável
 * - SOFT_SKILL      → habilidade comportamental
 * - EDUCATION       → grau acadêmico, área de formação
 * - EXPERIENCE      → indicador de nível/tempo de experiência
 * - LANGUAGE        → idioma humano
 * - JOB_CONDITION   → condição de trabalho (remoto, salário, benefícios)
 * - GENERAL         → outros termos sem categoria específica
 */
@Component
public class SkillClassifier {

    public enum TermCategory {
        HARD_SKILL,
        REQUIRED_SKILL,
        PREFERRED_SKILL,
        SOFT_SKILL,
        EDUCATION,
        EXPERIENCE,
        LANGUAGE,
        JOB_CONDITION,
        GENERAL
    }

    private static final Set<String> JOB_CONDITIONS    = buildJobConditions();
    private static final Set<String> HARD_SKILLS       = buildHardSkills();
    private static final Set<String> EDUCATION_TERMS   = buildEducationTerms();
    private static final Set<String> EXPERIENCE_TERMS  = buildExperienceTerms();
    private static final Set<String> LANGUAGE_TERMS    = buildLanguageTerms();
    private static final Set<String> SOFT_SKILLS       = buildSoftSkills();

    // ── Construtores de dicionários ────────────────────────────────────────

    private static Set<String> buildJobConditions() {
        return new HashSet<>(Arrays.asList(
            "remote", "hybrid", "flexible", "home", "office", "onsite",
            "schedule", "benefits", "salary", "compensation", "equity", "bonus",
            "working", "environment", "culture", "opportunity",
            "location", "relocation", "travel", "timezone", "async",
            "fulltime", "parttime", "contract", "freelance", "permanent",
            "temporary", "internship",
            "required", "preferred", "plus", "ideal", "nice",
            "looking", "seeking", "candidate", "applicant", "position", "role",
            "company", "startup", "enterprise", "global", "international",
            "medical", "dental", "vision", "insurance", "pto", "vacation",
            "stock", "options", "growth", "career", "join", "help"
        ));
    }

    private static Set<String> buildHardSkills() {
        return new HashSet<>(Arrays.asList(
            // Linguagens
            "java", "python", "javascript", "typescript", "kotlin", "scala",
            "go", "golang", "rust", "ruby", "php", "swift", "sql", "html",
            "css", "bash", "shell", "r", "matlab", "c", "cpp", "csharp",
            // Frameworks Java
            "spring", "springboot", "hibernate", "jpa",
            "spring-boot", "spring-data", "spring-security", "spring-mvc",
            "spring-cloud", "quarkus", "micronaut", "jakarta",
            // Frontend
            "react", "angular", "vue", "nextjs", "nodejs", "express",
            "tailwind", "bootstrap", "redux", "graphql", "webpack", "vite",
            // Bancos
            "postgresql", "mysql", "oracle", "mongodb", "redis",
            "elasticsearch", "cassandra", "dynamodb", "sqlite", "mariadb",
            "h2", "flyway", "liquibase", "sqlserver",
            // Cloud / DevOps
            "aws", "azure", "gcp", "docker", "kubernetes", "terraform",
            "jenkins", "ansible", "nginx", "linux", "railway", "heroku",
            "vercel", "netlify", "gitlab", "github-actions",
            // Arquitetura
            "microservices", "rest", "restful", "api", "soap", "grpc",
            "kafka", "rabbitmq", "activemq", "cqrs", "ddd", "tdd",
            "bdd", "solid", "oop", "mvc",
            // Testes
            "junit", "mockito", "testcontainers", "jest", "pytest",
            "selenium", "cypress", "cucumber",
            // Ferramentas
            "git", "maven", "gradle", "npm", "yarn", "postman",
            "jira", "confluence", "sonar", "intellij", "eclipse", "vscode",
            // IA/ML
            "tensorflow", "pytorch", "scikit-learn", "pandas", "numpy",
            "keras", "huggingface", "langchain",
            // Mobile
            "android", "ios", "flutter", "react-native",
            // Protocolos / conceitos técnicos
            "oauth", "jwt", "graphql", "websocket", "grpc", "http",
            "https", "tcp", "ssh", "flink", "spark", "hadoop", "airflow"
        ));
    }

    private static Set<String> buildEducationTerms() {
        return new HashSet<>(Arrays.asList(
            "bachelor", "bacharelado", "graduacao", "graduation",
            "master", "mestrado", "mba",
            "phd", "doctorate", "doutorado",
            "degree", "diploma",
            "university", "universidade", "faculdade", "college",
            "computer-science", "ciencia-da-computacao",
            "software-engineering", "engenharia-de-software",
            "information-systems", "sistemas-de-informacao",
            "mathematics", "statistics", "engineering"
        ));
    }

    private static Set<String> buildExperienceTerms() {
        return new HashSet<>(Arrays.asList(
            "junior", "senior", "mid", "pleno", "lead", "principal", "staff",
            "year", "years", "ano", "anos",
            "experience", "experiencia",
            "entry", "level"
        ));
    }

    private static Set<String> buildLanguageTerms() {
        return new HashSet<>(Arrays.asList(
            "english", "ingles", "portuguese", "portugues",
            "spanish", "espanhol", "french", "frances",
            "german", "alemao", "italian", "italiano",
            "mandarin", "japanese", "korean",
            "fluent", "intermediate", "basic", "advanced",
            "b1", "b2", "c1", "c2", "a1", "a2"
        ));
    }

    private static Set<String> buildSoftSkills() {
        return new HashSet<>(Arrays.asList(
            "communication", "leadership", "teamwork", "collaboration",
            "problem-solving", "analytical", "creativity", "adaptability",
            "proactive", "autonomous", "organized", "detail-oriented",
            "time-management", "critical-thinking", "empathy",
            "comunicacao", "lideranca", "trabalho-em-equipe"
        ));
    }

    // ── API pública ────────────────────────────────────────────────────────

    public TermCategory classify(String term) {
        String t = term.toLowerCase().trim().replace(" ", "-");
        if (JOB_CONDITIONS.contains(t))   return TermCategory.JOB_CONDITION;
        if (HARD_SKILLS.contains(t))      return TermCategory.HARD_SKILL;
        if (EDUCATION_TERMS.contains(t))  return TermCategory.EDUCATION;
        if (EXPERIENCE_TERMS.contains(t)) return TermCategory.EXPERIENCE;
        if (LANGUAGE_TERMS.contains(t))   return TermCategory.LANGUAGE;
        if (SOFT_SKILLS.contains(t))      return TermCategory.SOFT_SKILL;
        if (t.length() <= 2)              return TermCategory.GENERAL;
        return TermCategory.GENERAL;
    }

    public boolean isJobCondition(String term) {
        return classify(term) == TermCategory.JOB_CONDITION;
    }

    public boolean isHardSkill(String term) {
        return classify(term) == TermCategory.HARD_SKILL;
    }

    public boolean isEducation(String term) {
        return classify(term) == TermCategory.EDUCATION;
    }

    public boolean isExperience(String term) {
        return classify(term) == TermCategory.EXPERIENCE;
    }

    public boolean isLanguage(String term) {
        return classify(term) == TermCategory.LANGUAGE;
    }
}
