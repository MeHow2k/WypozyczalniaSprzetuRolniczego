# Etap 1: Budowanie aplikacji za pomocą Mavena
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etap 2: Uruchomienie aplikacji produkcyjnej
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

RUN mkdir Cert

EXPOSE 8443
ENTRYPOINT ["java", "-jar", "app.jar"]