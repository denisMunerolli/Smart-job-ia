# ---- Etapa 1: build ----
# Builda todos os módulos Maven a partir do código-fonte.
# Necessário porque o Render (e a maioria dos PaaS) builda a imagem direto do
# repositório — não existe um target/*.jar pronto como no Dockerfile da Fase 1.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copia primeiro só os pom.xml de cada módulo para aproveitar o cache de
# camadas do Docker: se o código mudar mas as dependências não, esta camada
# não é reconstruída.
COPY pom.xml .
COPY smartjobai-commons/pom.xml smartjobai-commons/
COPY smartjobai-core/pom.xml smartjobai-core/
COPY smartjobai-infrastructure/pom.xml smartjobai-infrastructure/
COPY smartjobai-ai/pom.xml smartjobai-ai/
COPY smartjobai-batch/pom.xml smartjobai-batch/
COPY smartjobai-api/pom.xml smartjobai-api/
RUN mvn -B dependency:go-offline

# Agora copia o código-fonte completo e builda o jar executável do módulo api
COPY smartjobai-commons smartjobai-commons
COPY smartjobai-core smartjobai-core
COPY smartjobai-infrastructure smartjobai-infrastructure
COPY smartjobai-ai smartjobai-ai
COPY smartjobai-batch smartjobai-batch
COPY smartjobai-api smartjobai-api
RUN mvn -B clean package -DskipTests

# ---- Etapa 2: runtime ----
# Imagem final enxuta, só com o JRE e o jar — não carrega o Maven nem o
# código-fonte, então fica bem menor que a imagem de build.
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=build /workspace/smartjobai-api/target/smartjobai-api-*.jar app.jar

# Roda como usuário não-root por segurança
RUN useradd --create-home --shell /bin/bash appuser && chown appuser:appuser app.jar
USER appuser

# O Render injeta a variável PORT dinamicamente; o application-prod.yml lê
# ${PORT:8080}, então isto funciona tanto no Render quanto localmente.
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]
