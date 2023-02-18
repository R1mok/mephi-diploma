FROM adoptopenjdk/openjdk11:ubi
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} pet-manager.jar
ENTRYPOINT ["java","-jar","pet-manager.jar"]