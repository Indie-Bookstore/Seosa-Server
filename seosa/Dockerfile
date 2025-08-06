#FROM --platform=linux/amd64 eclipse-temurin:17-jdk-alpine

#RUN apk add --no-cache ca-certificates curl openssl

#COPY ./build/libs/*SNAPSHOT.jar project.jar

#ENTRYPOINT exec java $JAVA_OPTS -jar project.jar

FROM eclipse-temurin:17-jdk

COPY ./build/libs/seosa-0.0.1-SNAPSHOT.jar project.jar

ENTRYPOINT ["java", "-jar", "project.jar"]