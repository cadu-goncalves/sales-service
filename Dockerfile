FROM        openjdk:8-jdk-alpine

ENV         OS_PACKAGES "curl"
ENV         APP_HOME /home/sales-service
ARG         JAR_FILE

# Dependencies
RUN         apk add ${OS_PACKAGES} --update --no-cache && \
            rm -rf /var/cache/apk

# Group & user
RUN         addgroup -S -g 1000 service-user && \
            adduser -S -u 1000 -G service-user -h ${APP_HOME} service-user

# Configure
WORKDIR     ${APP_HOME}
USER        service-user
VOLUME      /tmp
COPY        ${JAR_FILE} service.jar

# Health
HEALTHCHECK CMD curl http://localhost:8080/actuator/health

# Run
ENTRYPOINT  ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/service.jar"]
