# 1. ビルド用
FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 2. 実行用
FROM openjdk:17-jdk-slim
# 実行時の作業場所を /app に固定する
WORKDIR /app
# ビルドした場所から、今の作業場所にコピーする
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# 同じ場所にある app.jar を実行する
ENTRYPOINT ["java", "-jar", "app.jar"]
