FROM amazoncorretto:23.0.1-al2023-headful

USER root

COPY ./target/learning-core-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080 8081

ENTRYPOINT ["java", "-jar", "/app.jar"]
