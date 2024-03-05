FROM gradle:8.5.0-jdk21 as BUILD
MAINTAINER "Sven Dukker"
WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
RUN ./gradlew build || exit 0  # try catch -> should only install dependencies

COPY scripts/run.sh scripts/run.sh
CMD scripts/run.sh

