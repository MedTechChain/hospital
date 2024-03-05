FROM openjdk:17-jdk-slim
MAINTAINER "Sven"

RUN ./gradlew build

CMD ["ls"]
