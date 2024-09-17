FROM openjdk:17-jdk-slim
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Add remote debugging support
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005

# Enable devtools for hot reload
ENTRYPOINT ["java", "-Dspring.devtools.restart.enabled=true", "-Dspring.devtools.livereload.enabled=true", "-jar", "/app.jar"]
