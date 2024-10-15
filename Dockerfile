# Build stage
FROM maven:3.8.5-openjdk-21 AS build
WORKDIR /app
COPY pom.xml . 
COPY src src 

# Defining build-time variables
ARG SPRING_PROFILES_ACTIVE
ARG SPRING_DATASOURCE_URL
ARG SPRING_DATASOURCE_USERNAME
ARG SPRING_DATASOURCE_PASSWORD
ARG SPRING_JPA_SHOW_SQL
ARG SPRING_DATA_REDIS_HOST

# Passing ARG variables to ENV so they are available at runtime
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}
ENV SPRING_DATASOURCE_URL=${SPRING_DATASOURCE_URL}
ENV SPRING_DATASOURCE_USERNAME=${SPRING_DATASOURCE_USERNAME}
ENV SPRING_DATASOURCE_PASSWORD=${SPRING_DATASOURCE_PASSWORD}
ENV SPRING_JPA_SHOW_SQL=${SPRING_JPA_SHOW_SQL}
ENV SPRING_DATA_REDIS_HOST=${SPRING_DATA_REDIS_HOST}

# Building the application, skipping tests
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copying the built JAR from the build stage to the runtime stage
COPY --from=build /app/target/*.jar app.jar 

# Exposing the application port
EXPOSE 8080 

# Entry command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
