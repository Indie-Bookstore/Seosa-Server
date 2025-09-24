FROM eclipse-temurin:17-jdk-alpine

RUN apk add --no-cache ca-certificates curl openssl tzdata \
 && cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime \
 && echo "Asia/Seoul" > /etc/timezone

COPY ./build/libs/*SNAPSHOT.jar project.jar

ENTRYPOINT exec java $JAVA_OPTS -jar project.jar

