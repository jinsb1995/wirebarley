FROM ubuntu:22.04

RUN apt-get update && \
	apt-get -y install openjdk-17-jdk

ARG JAR_FILE=build/libs/wirebarley-*-SNAPSHOT.jar

RUN mkdir /home/wirebarley

WORKDIR /home/wirebarley

COPY $JAR_FILE /home/wirebarley/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
