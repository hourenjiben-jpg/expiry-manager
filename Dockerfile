# 1. ビルド用
FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 2. 実行用 (ここを修正しました)
FROM eclipse-temurin:17-jre-slim
WORKDIR /app
# ビルドステージから jar ファイルをコピー
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

