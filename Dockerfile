FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/acme.jar /app/
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/acme.jar"]
