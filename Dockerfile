FROM gradle:8.6.0-jdk21 AS builder

COPY --chown=gradle:gradle . /home/gradle/src

WORKDIR /home/gradle/src

RUN gradle build --no-daemon

FROM eclipse-temurin:21-jdk-jammy

COPY --from=builder /home/gradle/src/build/libs/*.jar /app/spring-boot-application.jar

WORKDIR /app

EXPOSE 8080

CMD ["java", "-jar", "/app/spring-boot-application.jar"]