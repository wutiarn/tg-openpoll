FROM java:8-jdk

RUN apt-get update
RUN apt-get install libgs-dev zbar-tools -y

WORKDIR /code
ADD . /code

RUN ./gradlew build

RUN mv build/dist/tg-openpoll.jar .

CMD java -jar tg-openpoll.jar