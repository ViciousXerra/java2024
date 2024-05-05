FROM eclipse-temurin:21-jre
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
COPY ./target/scrapper.jar $HOME
EXPOSE 8080 8081
ENTRYPOINT java -jar scrapper.jar
