FROM gradle:6.6.1-jdk11

COPY . /work 
WORKDIR /work

RUN ./gradlew build

CMD ["./gradlew", ":run", "--console=plain"]