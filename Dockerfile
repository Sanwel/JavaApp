FROM java:8
ENV APP_JAR="opt/myapp/app.jar"
ADD target/rd-1.0-SNAPSHOT.jar  /opt/myapp/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/opt/myapp/app.jar"]
