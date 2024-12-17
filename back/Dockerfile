FROM openjdk:11-jdk-slim

WORKDIR /app

COPY target/yoga-app-0.0.1-SNAPSHOT.jar yoga.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "yoga.jar"]
