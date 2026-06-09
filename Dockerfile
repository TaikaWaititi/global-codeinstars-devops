FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /workspace
COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY src src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre

ENV APP_HOME=/opt/sistema-helios
WORKDIR ${APP_HOME}

RUN groupadd --system helios && useradd --system --gid helios --home-dir ${APP_HOME} helios

COPY --from=build --chown=helios:helios /workspace/target/*.jar app.jar

USER helios
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
