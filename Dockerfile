FROM openjdk:21-jdk-slim

WORKDIR /app

COPY build/libs/the_monitor-0.0.1.jar /app/the_monitor.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/the_monitor.jar"]