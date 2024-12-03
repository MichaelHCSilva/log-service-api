FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/log-service-api-0.0.1-SNAPSHOT.jar log-service-api.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "log-service-api.jar"]
