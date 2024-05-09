FROM openjdk:18-alpine
VOLUME /clnArch
ENV REPO_DIR /var/repo
ENV LOG_DIR var/logs
COPY build/libs/endpoints-1.0.0.jar cleanArch.jar
ENTRYPOINT ["java", "-jar", "/cleanArch.jar"]