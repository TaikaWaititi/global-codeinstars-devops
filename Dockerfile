FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /workspace
COPY pom.xml mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY src src
RUN mvn -B clean package -DskipTests
RUN jlink \
    --add-modules java.base,java.logging,java.sql,java.naming,java.management,java.instrument,java.desktop,java.xml,java.compiler,java.security.jgss,java.transaction.xa,jdk.unsupported,jdk.crypto.ec,jdk.zipfs,jdk.charsets \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /opt/java-runtime

FROM alpine:3.23

ENV APP_HOME=/opt/sistema-helios
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"
WORKDIR ${APP_HOME}

RUN apk add --no-cache ca-certificates tzdata \
    && addgroup -S helios \
    && adduser -S -G helios -h ${APP_HOME} helios

COPY --from=build /opt/java-runtime ${JAVA_HOME}
COPY --from=build --chown=helios:helios /workspace/target/*.jar app.jar

USER helios
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
