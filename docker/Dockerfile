# ---- Etapa 1: Build ----
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copia todo o código para o container
COPY . .

# Compila todos os módulos e gera o JAR executável diretamente
RUN mvn -B clean package -DskipTests

# ---- Etapa 2: Runtime ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia o JAR compilado do módulo API
COPY --from=build /workspace/smartjobai-api/target/smartjobai-api-*.jar app.jar
# ---- Etapa 1: Build ----
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copia todo o código-fonte do projeto multi-módulo
COPY . .

# Compila o projeto raiz e todos os submódulos ignorando testes para acelerar
RUN mvn -B clean package -DskipTests

# ---- Etapa 2: Runtime ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copia o JAR executável gerado especificamente no submódulo smartjobai-api
COPY --from=build /workspace/smartjobai-api/target/smartjobai-api-*.jar app.jar

# Configurações de execução
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]
# Configurações de execução
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]