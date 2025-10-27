# ============================================
# Stage 1: Build da aplicação
# ============================================
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copia os arquivos do Maven
COPY pom.xml .
COPY src ./src

# Compila a aplicação
RUN mvn clean package -DskipTests

# ============================================
# Stage 2: Imagem final (runtime)
# ============================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copia o JAR compilado do stage anterior
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta da aplicação
EXPOSE 8080

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
