FROM openjdk:17
WORKDIR /app
copy target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
