# 빌드 단계에서 OpenJDK 17 slim 이미지를 베이스로 사용합니다
FROM --platform=linux/amd64 openjdk:17-jdk-slim AS build

# 컨테이너 내 작업 디렉토리를 설정합니다
WORKDIR /app

# JAR 파일 위치를 정의하는 인수를 설정합니다
ARG JAR_FILE=build/libs/*.jar

# 빌드 컨텍스트에서 컨테이너로 JAR 파일을 복사합니다
COPY ${JAR_FILE} app.jar

# 런타임 단계
FROM --platform=linux/amd64 openjdk:17-jdk-slim

# 컨테이너 내 작업 디렉토리를 설정합니다
WORKDIR /app

# 애플리케이션 설정 파일을 컨테이너로 복사합니다
COPY src/main/resources/application.yml /app/application.yml

# 빌드 단계에서 런타임 단계로 JAR 파일을 복사합니다
COPY --from=build /app/app.jar app.jar

# 애플리케이션을 위해 포트 8080을 노출합니다
EXPOSE 8080

# 컨테이너의 엔트리포인트를 정의합니다
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dotel.resource.attributes=service.name=auth-server", "-jar", "app.jar"]
