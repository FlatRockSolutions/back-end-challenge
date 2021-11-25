FROM openjdk:jdk-12.0.2_10-alpine
MAINTAINER andrewkulminsky
COPY target/payprovider-challenge-be-1.0.jar executable.jar
ENTRYPOINT ["java","-jar","/executable.jar"]