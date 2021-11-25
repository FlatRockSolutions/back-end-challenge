FROM openjdk:12-jdk-alpine
MAINTAINER andrewkulminsky
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]