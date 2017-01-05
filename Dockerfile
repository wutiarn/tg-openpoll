FROM openjdk:8-alpine

RUN apk add --no-cache bash

WORKDIR /code
ADD . /code

RUN ./gradlew build &&\
    rm -rf /root/.gradle &&\
    mv build/dist/tg-openpoll.jar . &&\
    rm -rf build/ .gradle/

CMD java -jar tg-openpoll.jar