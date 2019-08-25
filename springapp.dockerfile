FROM java:8
VOLUME /vol/development
ARG JAR_FILE
COPY /target/${JAR_FILE} /vol/development/
RUN sh -c 'touch JavaApplication-0.0.1-SNAPSHOT.jar'
WORKDIR /vol/development
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "JavaApplication-0.0.1-SNAPSHOT.jar"]

