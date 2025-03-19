# JDK 17을 이용한 executable jar 빌드 단계
FROM eclipse-temurin:17-alpine AS build
RUN apk add --no-cache bash

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
# Kotlin 프로젝트의 경우 .kts 확장자를 적용
# COPY build.gradle.kts settings.gradle.kts ./

RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon

COPY . .

RUN ./gradlew bootJar --no-daemon

# 실행을 위한 JRE 17 Alpine 기반 이미지
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Chrome 및 ChromeDriver 설치
RUN apk add --no-cache \
    chromium \
    chromium-chromedriver \
    && ln -s /usr/bin/chromedriver /usr/local/bin/chromedriver

# 사용자 추가
RUN addgroup -g 1000 worker && \
    adduser -u 1000 -G worker -s /bin/sh -D worker

COPY --from=build --chown=worker:worker /app/build/libs/*.jar ./main.jar

USER worker:worker

# 8080 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "main.jar"]
