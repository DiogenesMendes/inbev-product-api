FROM openjdk:8-jre
RUN mkdir app
ARG JAR_FILE
ADD /target/${JAR_FILE} /app/product-api.jar
WORKDIR /app
ENTRYPOINT java -jar product-api.jar