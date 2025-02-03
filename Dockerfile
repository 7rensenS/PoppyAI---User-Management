# Stage 1: Build the JAR file
FROM maven:3.8.6-amazoncorretto-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests  # Build the JAR (skip tests for faster build)

# Stage 2: Create the final image
FROM amazoncorretto:17-alpine-jdk
WORKDIR /app
COPY --from=build /app/target/user-management-0.0.1-SNAPSHOT.jar /app/app.jar


EXPOSE 8080


CMD ["java", "-jar", "/app/app.jar"]