# 1. 組み立て用の環境
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 2. 実行用の環境
FROM openjdk:17.0.1-jdk-slim
WORKDIR /app
# targetフォルダの中にある .jar ファイルを app.jar という名前でコピーする
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]