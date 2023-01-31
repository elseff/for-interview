FROM openjdk:17-alpine

EXPOSE 8070

COPY target/*.jar /demo-app/app.jar

VOLUME /demo-app

WORKDIR /demo-app

CMD java -jar app.jar