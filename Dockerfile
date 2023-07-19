FROM gradle:7.4.0-jdk17

WORKDIR /app

COPY ./ .

RUN gradle clean installDist

CMD ./build/install/movies_diary/bin/movies_diary

EXPOSE 8000