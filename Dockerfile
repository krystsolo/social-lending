FROM openjdk:11-jdk
EXPOSE 8080
COPY ${JAR_FILE} ${JAR_FILE}
ADD target/dragons-lending-api-*.jar dragons-lending-api.jar
ENTRYPOINT ["java","-jar","/dragons-lending-api.jar"]
