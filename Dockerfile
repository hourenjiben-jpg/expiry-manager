# 1. ビルド用（Amazonが提供する安定したJava環境を使用）
FROM amazoncorretto:17-al2023-jdk AS build
WORKDIR /app
COPY . .
# 実行権限を付与してビルド
RUN chmod +x ./mvnw && ./mvnw clean package -DskipTests

# 2. 実行用
FROM amazoncorretto:17-al2023-headless
WORKDIR /app
# targetフォルダ内のjarをapp.jarとしてコピー
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]


