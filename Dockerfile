# 1. ビルド用
FROM maven:3.8.5-openjdk-17 AS build
COPY . /home/app
RUN mvn -f /home/app/pom.xml clean package -DskipTests

# 2. 実行用
FROM openjdk:17.0.1-jdk-slim
# ビルドで作られたjarファイルを確実にコピーする
COPY --from=build /home/app/target/*.jar /usr/local/lib/app.jar
EXPOSE 8080
# フルパスで指定して実行する
ENTRYPOINT ["java","-jar","/usr/local/lib/app.jar"]