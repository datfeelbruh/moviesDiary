FROM gradle:7.4.0-jdk17
ADD /build/libs/movies_diary-1.0.jar backend.jar
ENTRYPOINT ["java", "-jar", "backend.jar"]
