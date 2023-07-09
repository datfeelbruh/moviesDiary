FROM gradle:7.4.0-jdk17
ARG JAR_FIKE=build/libs/*.jar
COPY ${JAR_FILE} movies_diary-1.0.0.jar
EXPOSE 5000
ENTRYPOINT ["java", "-jar", "movies_diary-1.0.0.jar"]
