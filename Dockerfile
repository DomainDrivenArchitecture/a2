FROM openjdk:8-alpine

COPY target/uberjar/job-board.jar /job-board/app.jar

EXPOSE 8080

CMD ["java", "-jar", "/job-board/app.jar"]
