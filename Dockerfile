FROM openjdk:17
WORKDIR /app
copy target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]