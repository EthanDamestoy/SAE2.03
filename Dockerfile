FROM debian:latest

WORKDIR /app

RUN apt-get update && \
    apt-get install -y openjdk-17-jdk && \
    apt-get clean

COPY ./ . 

RUN javac -encoding UTF-8 -d class @Compile.list

EXPOSE 9000

ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-cp", "class", "Controleur"]
