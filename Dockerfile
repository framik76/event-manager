FROM maven:3.8.2-openjdk-17-slim as builder

# Copy local code to the container image.
WORKDIR /app
COPY pom.xml ./
COPY src ./src/

# Build a release artifact.
RUN mvn package -DskipTests

#FROM openjdk:17-alpine
FROM bellsoft/liberica-openjdk-alpine:17

# Copy the jar to the production image from the builder stage.
COPY --from=builder /app/target/event-manager-*.jar /event-manager.jar

# Run the web service on container startup.
CMD ["java","-Dserver.port=${PORT}","-jar","/event-manager.jar"]