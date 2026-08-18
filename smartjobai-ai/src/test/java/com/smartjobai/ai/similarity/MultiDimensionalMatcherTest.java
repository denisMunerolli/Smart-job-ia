package com.smartjobai.ai.similarity;

import com.smartjobai.ai.classifier.SkillClassifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de regressão do MultiDimensionalMatcher.
 * Estes testes documentam o comportamento atual e servem como
 * linha de base para evitar regressões durante a evolução do algoritmo.
 */
class MultiDimensionalMatcherTest {

    private MultiDimensionalMatcher matcher;

    // Currículo de referência — Java Backend Junior
    private static final String CV_JAVA_JUNIOR = """
        Denis Munerolli
        Junior Java Backend Developer
        Software Engineering student
        
        Technical Skills:
        Java, Spring Boot, Spring Data JPA, Hibernate, REST APIs
        PostgreSQL, MySQL, SQL, Git, Maven, Docker
        Object-Oriented Programming, Collections, Generics
        Exception Handling, Interfaces, Abstract Classes
        JUnit, Mockito, Unit Testing
        
        Experience:
        Developed backend applications using Java 17 and Spring Boot
        Implemented REST APIs for simulation and integration
        Built modular backend architecture with separate modules
        Worked with PostgreSQL schema validation and Hibernate mappings
        Used Docker and Railway for deployment and testing
        
        Education:
        Bachelor of Software Engineering — Faculdade Estácio (in progress)
        """;

    private static final String CV_VAZIO = "";

    private static final String CV_INCOMPATIVEL = """
        Maria Santos — UX Designer
        Figma, Adobe XD, Sketch, Photoshop, Illustrator
        User research, wireframing, prototyping
        Design systems, accessibility, usability testing
        """;

    private static final String VAGA_JAVA_JUNIOR = """
        Junior Java Backend Developer
        
        Requirements:
        Java 11+ and Spring Boot development experience
        REST APIs design and implementation
        Spring Data JPA and Hibernate for database persistence
        PostgreSQL or MySQL relational databases
        Git version control
        Object-Oriented Programming: interfaces, inheritance, generics
        Exception handling and debugging skills
        Maven for build management
        Docker basics
        
        Preferred:
        Microservices architecture experience
        JUnit and Mockito for unit testing
        Cloud deployment: AWS, Railway, Heroku
        SOLID principles
        
        Bachelor degree in Software Engineering preferred.
        1-2 years of Java backend experience.
        """;

    private static final String VAGA_SENIOR = """
        Senior Java Architect — 10 years experience required
        
        Requirements:
        Java 17+ expert level
        Spring Boot, Spring Cloud, Spring Security
        Microservices architecture design
        Kafka, RabbitMQ messaging
        Kubernetes, AWS, Terraform infrastructure
        PostgreSQL, MongoDB, Redis
        10 years professional software engineering experience
        
        Must have:
        Architecture decision records
        Technical leadership of teams of 10+
        Performance optimization at scale
        """;

    private static final String VAGA_PYTHON = """
        Python Data Scientist
        
        Requirements:
        Python 3.10+
        Pandas, NumPy, Scikit-learn
        TensorFlow or PyTorch
        SQL and data manipulation
        Statistical analysis
        Machine learning algorithms
        Jupyter notebooks
        
        PhD or Master's degree in Statistics, Mathematics or Computer Science preferred.
        """;

    @BeforeEach
    void setUp() {
        SkillClassifier classifier = new SkillClassifier();
        TFIDFMatcher tfidf = new TFIDFMatcher();
        matcher = new MultiDimensionalMatcher(tfidf, classifier);
    }

    @Nested
    @DisplayName("CV perfeito para a vaga")
    class CvPerfeito {

        @Test
        @DisplayName("deve retornar score alto para CV alinhado com a vaga")
        void cvAlinhado_deveRetornarScoreAlto() {
            var result = matcher.calcular(VAGA_JAVA_JUNIOR, CV_JAVA_JUNIOR);
            assertThat(result.scoreGeral())
                .as("Score geral deve ser >= 60 para CV alinhado")
                .isGreaterThanOrEqualTo(60);
        }

        @Test
        @DisplayName("nivel deve ser MEDIO ou ALTO para CV alinhado")
        void cvAlinhado_nivelDeveSerMedioOuAlto() {
            var result = matcher.calcular(VAGA_JAVA_JUNIOR, CV_JAVA_JUNIOR);
            assertThat(result.nivel())
                .as("Nível deve ser MEDIO ou ALTO")
                .isIn("MEDIO", "ALTO");
        }

        @Test
        @DisplayName("hard skills devem ter score alto para tecnologias presentes")
        void cvAlinhado_hardSkillsAltas() {
            var result = matcher.calcular(VAGA_JAVA_JUNIOR, CV_JAVA_JUNIOR);
            assertThat(result.hardSkills())
                .as("Hard skills devem ser >= 70 quando as tecnologias estão presentes")
                .isGreaterThanOrEqualTo(70);
        }

        @Test
        @DisplayName("nao deve listar Java como habilidade faltante")
        void cvAlinhado_javaNaoDeveEstarNasFaltantes() {
            var result = matcher.calcular(VAGA_JAVA_JUNIOR, CV_JAVA_JUNIOR);
            assertThat(result.hardSkillsFaltantes())
                .as("Java não deve estar nas habilidades faltantes")
                .doesNotContain("java");
        }

        @Test
        @DisplayName("nao deve listar Spring Boot como habilidade faltante")
        void cvAlinhado_springBootNaoDeveEstarNasFaltantes() {
            var result = matcher.calcular(VAGA_JAVA_JUNIOR, CV_JAVA_JUNIOR);
            assertThat(result.hardSkillsFaltantes())
                .as("Spring Boot não deve estar nas faltantes")
                .doesNotContain("spring", "springboot", "spring-boot");
        }
    }

    @Nested
    @DisplayName("CV incompatível com a vaga")
    class CvIncompativel {

        @Test
        @DisplayName("score deve ser baixo para CV de UX Designer em vaga Java")
        void cvIncompativel_deveRetornarScoreBaixo() {
            var result = matcher.calcular(VAGA_JAVA_JUNIOR, CV_INCOMPATIVEL);
            assertThat(result.scoreGeral())
                .as("Score deve ser <= 40 para CV completamente incompatível")
                .isLessThanOrEqualTo(40);
        }

        @Test
        @DisplayName("nivel deve ser BAIXO para CV incompativel")
        void cvIncompativel_nivelDeveBaixo() {
            var result = matcher.calcular(VAGA_JAVA_JUNIOR, CV_INCOMPATIVEL);
            assertThat(result.nivel()).isEqualTo("BAIXO");
        }

        @Test
        @DisplayName("Java deve aparecer como hard skill faltante")
        void cvIncompativel_javaDeveEstarNasFaltantes() {
            var result = matcher.calcular(VAGA_JAVA_JUNIOR, CV_INCOMPATIVEL);
            assertThat(result.hardSkillsFaltantes())
                .as("Java deve aparecer como faltante")
                .contains("java");
        }
    }

    @Nested
    @DisplayName("CV vazio")
    class CvVazio {

        @Test
        @DisplayName("score deve ser baixo para CV vazio")
        void cvVazio_deveRetornarScoreBaixo() {
            var result = matcher.calcular(VAGA_JAVA_JUNIOR, CV_VAZIO);
            assertThat(result.scoreGeral())
                .as("CV vazio deve ter score <= 30")
                .isLessThanOrEqualTo(30);
        }

        @Test
        @DisplayName("nao deve lancar excecao para CV vazio")
        void cvVazio_naoDeveLancarExcecao() {
            org.junit.jupiter.api.Assertions.assertDoesNotThrow(
                () -> matcher.calcular(VAGA_JAVA_JUNIOR, CV_VAZIO)
            );
        }
    }

    @Nested
    @DisplayName("Vaga Senior vs CV Junior")
    class VagaSeniorCvJunior {

        @Test
        @DisplayName("score deve ser menor que para vaga junior equivalente")
        void vagaSenior_cvJunior_deveScoreMenorQueVagaJunior() {
            var resultJunior = matcher.calcular(VAGA_JAVA_JUNIOR, CV_JAVA_JUNIOR);
            var resultSenior = matcher.calcular(VAGA_SENIOR, CV_JAVA_JUNIOR);

            assertThat(resultJunior.scoreGeral())
                .as("Score para vaga junior deve ser maior que para vaga senior")
                .isGreaterThan(resultSenior.scoreGeral());
        }
    }

    @Nested
    @DisplayName("Vaga Python vs CV Java")
    class VagaPythonCvJava {

        @Test
        @DisplayName("score deve ser baixo para CV Java em vaga Python DS")
        void vagaPython_cvJava_deveScoreBaixo() {
            var result = matcher.calcular(VAGA_PYTHON, CV_JAVA_JUNIOR);
            assertThat(result.scoreGeral())
                .as("CV Java em vaga Python DS deve ter score <= 45")
                .isLessThanOrEqualTo(45);
        }

        @Test
        @DisplayName("python deve aparecer como hard skill faltante")
        void vagaPython_cvJava_pythonDeveEstarNasFaltantes() {
            var result = matcher.calcular(VAGA_PYTHON, CV_JAVA_JUNIOR);
            assertThat(result.hardSkillsFaltantes())
                .as("Python deve aparecer como faltante")
                .contains("python");
        }
    }

    @Nested
    @DisplayName("Termos de contexto")
    class TermosContexto {

        @Test
        @DisplayName("remote nao deve aparecer como hard skill faltante")
        void remote_naoDeveSerHardSkillFaltante() {
            String vagaComRemote = VAGA_JAVA_JUNIOR + "\nRemote work, flexible schedule, home office, benefits";
            var result = matcher.calcular(vagaComRemote, CV_JAVA_JUNIOR);
            assertThat(result.hardSkillsFaltantes())
                .as("Palavras de contexto não devem aparecer como faltantes")
                .doesNotContain("remote", "flexible", "home", "benefits", "schedule");
        }

        @Test
        @DisplayName("palavras de contexto devem ser identificadas e separadas")
        void palavrasContexto_devemSerIdentificadas() {
            String vagaComContexto = VAGA_JAVA_JUNIOR + "\nRemote work, flexible schedule, home office";
            var result = matcher.calcular(vagaComContexto, CV_JAVA_JUNIOR);
            assertThat(result.skillsContexto())
                .as("Palavras de contexto devem ser listadas separadamente")
                .isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Consistencia do score")
    class Consistencia {

        @Test
        @DisplayName("score deve estar sempre entre 0 e 100")
        void score_sempreEntre0e100() {
            var r1 = matcher.calcular(VAGA_JAVA_JUNIOR, CV_JAVA_JUNIOR);
            var r2 = matcher.calcular(VAGA_SENIOR, CV_INCOMPATIVEL);
            var r3 = matcher.calcular(VAGA_PYTHON, CV_VAZIO);

            for (var r : new MultiDimensionalMatcher.MatchingDetalhado[]{r1, r2, r3}) {
                assertThat(r.scoreGeral()).isBetween(0, 100);
                assertThat(r.hardSkills()).isBetween(0, 100);
                assertThat(r.qualificacoesRequeridas()).isBetween(0, 100);
                assertThat(r.experiencia()).isBetween(0, 100);
                assertThat(r.educacao()).isBetween(0, 100);
                assertThat(r.preferencias()).isBetween(0, 100);
                assertThat(r.similaridadeTexto()).isBetween(0, 100);
            }
        }

        @Test
        @DisplayName("nivel deve ser ALTO para score >= 70")
        void nivel_altoParaScore70() {
            // Usar o melhor CV possível para garantir score alto
            var result = matcher.calcular(VAGA_JAVA_JUNIOR, CV_JAVA_JUNIOR);
            if (result.scoreGeral() >= 70) {
                assertThat(result.nivel()).isEqualTo("ALTO");
            }
        }

        @Test
        @DisplayName("nivel deve ser BAIXO para score < 40")
        void nivel_baixoParaScoreAbaixo40() {
            var result = matcher.calcular(VAGA_JAVA_JUNIOR, CV_VAZIO);
            if (result.scoreGeral() < 40) {
                assertThat(result.nivel()).isEqualTo("BAIXO");
            }
        }

        @Test
        @DisplayName("mesmo input deve sempre retornar mesmo score")
        void mesmosInputs_mesmosScores() {
            var r1 = matcher.calcular(VAGA_JAVA_JUNIOR, CV_JAVA_JUNIOR);
            var r2 = matcher.calcular(VAGA_JAVA_JUNIOR, CV_JAVA_JUNIOR);
            assertThat(r1.scoreGeral()).isEqualTo(r2.scoreGeral());
            assertThat(r1.hardSkills()).isEqualTo(r2.hardSkills());
        }
    }
}
