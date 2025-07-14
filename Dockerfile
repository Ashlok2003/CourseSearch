# ---------------Stage 1: Build Application--------------------
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

RUN apk add --no-cache maven

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline -B

COPY src ./src

RUN ./mvnw clean package -DskipTests

# ---------------Stage 2: Start Application--------------------
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

RUN adduser -D appuser
USER appuser

COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT exec java $JAVA_OPTS -jar app.jar
