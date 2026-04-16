# 1. 組み立て（ビルド）用の環境
FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app
COPY . .
# 権限問題を避けるため、標準のmvnコマンドでビルド
RUN mvn clean package -DskipTests

# 2. 実行用の環境
FROM eclipse-temurin:17-jre-slim
WORKDIR /app
# 出来上がったjarファイルをapp.jarという名前でコピー
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# アプリを起動
ENTRYPOINT ["java", "-jar", "app.jar"]


