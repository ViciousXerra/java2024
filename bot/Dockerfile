FROM eclipse-temurin:21-jre
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
COPY ./target/bot.jar $HOME
EXPOSE 8090 8091
ENTRYPOINT java -jar bot.jar
