FROM eclipse-temurin:17-jdk-alpine

RUN apk add --no-cache ca-certificates curl openssl

COPY ./build/libs/*SNAPSHOT.jar project.jar

ENTRYPOINT exec java $JAVA_OPTS -jar project.jar

