FROM maven:3.8.6-eclipse-temurin-17 as build
WORKDIR /workspace/app
COPY pom.xml .
COPY src src
RUN mvn clean package -Dmaven.test.skip=true

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/app/target/*.jar app.jar
RUN apt-get update && apt-get install -y curl wget && rm -rf /var/lib/apt/lists/*
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"] 