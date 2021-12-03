FROM openjdk:17-jdk-alpine
ENV APP_FILE app.jar
ENV APP_HOME /usr/app
COPY target/*.jar $APP_HOME/$APP_FILE
WORKDIR $APP_HOME
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar $APP_FILE ${0} ${@}"]
