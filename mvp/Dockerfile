# Dockerfile Multi stage
FROM maven:3.9.14-eclipse-temurin-21 AS builder

WORKDIR /app 
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q package -DskipTests

COPY --from=builder /app/target/mvp-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]


