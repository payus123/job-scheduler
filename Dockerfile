FROM adoptopenjdk/openjdk11
VOLUME /tmp
COPY target/background-service-0.0.1-SNAPSHOT.jar background-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "background-service.jar"]
