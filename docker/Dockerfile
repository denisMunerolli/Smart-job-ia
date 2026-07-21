# ---- Etapa 1: build ----
# Builda todos os módulos Maven a partir do código-fonte.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copia primeiro o pom.xml pai e os pom.xml dos módulos para aproveitar o cache
COPY pom.xml .
COPY smartjobai-commons/pom.xml smartjobai-commons/
COPY smartjobai-core/pom.xml smartjobai-core/
COPY smartjobai-infrastructure/pom.xml smartjobai-infrastructure/
COPY smartjobai-ai/pom.xml smartjobai-ai/
COPY smartjobai-batch/pom.xml smartjobai-batch/
COPY smartjobai-api/pom.xml smartjobai-api/

# Baixa as dependências e plugins externos para o cache do Docker
RUN mvn -B dependency:resolve-plugins dependency:resolve -DexcludeGroupIds=com.smartjobai || true

# Copia o código-fonte completo dos módulos
COPY smartjobai-commons smartjobai-commons
COPY smartjobai-core smartjobai-core
COPY smartjobai-infrastructure smartjobai-infrastructure
COPY smartjobai-ai smartjobai-ai
COPY smartjobai-batch smartjobai-batch
COPY smartjobai-api smartjobai-api

# Compila o projeto e gera o .jar do módulo API
RUN mvn -B clean package -DskipTests

# ---- Etapa 2: runtime ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /workspace/smartjobai-api/target/smartjobai-api-*.jar app.jar

# Roda como usuário não-root por segurança
RUN useradd --create-home --shell /bin/bash appuser && chown appuser:appuser app.jar
USER appuser

EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]