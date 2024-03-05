FROM openjdk:21-jdk-slim
MAINTAINER "Sven"

#CMD ["ls"]
CMD ["java", "-cp", "hospital-server.main", "com.hospital.server.DemoApplication"]
