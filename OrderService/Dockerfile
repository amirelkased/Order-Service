FROM openjdk:21-jdk-oracle
LABEL authors="amirelkased"
LABEL maintainer="Amir Elkased"
WORKDIR /app
COPY target/OrderService-0.0.1-SNAPSHOT.jar .
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "OrderService-0.0.1-SNAPSHOT.jar"]