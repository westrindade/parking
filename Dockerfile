FROM openjdk:17-jdk-alpine
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ./target/parking-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
#cria a imagem
#docker build -t wesmax/api-parking .
#executa a imagem
#docker run -p 8080:8080 api-parking