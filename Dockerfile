# --- Build stage ---
FROM eclipse-temurin:17-jdk AS build
RUN apt-get update && apt-get install -y --no-install-recommends unzip && rm -rf /var/lib/apt/lists/*
WORKDIR /workspace

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B -q

COPY src src
RUN ./mvnw package -DskipTests -B -q

# --- Runtime stage ---
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /workspace/target/game-sales-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
