# Build stage
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY . .
RUN ./gradlew bootJar

# Runtime stage
FROM ubuntu:22.04

# 기본 패키지 설치 + Chrome 설치 + Chromedriver 세팅
RUN apt-get update && apt-get install -y \
    wget curl unzip gnupg2 ca-certificates \
    fonts-liberation libappindicator3-1 libasound2 libatk-bridge2.0-0 libatk1.0-0 libgbm1 libnspr4 libnss3 libx11-xcb1 libxcomposite1 libxdamage1 libxrandr2 libxss1 libxtst6 lsb-release xdg-utils chromium-driver chromium-browser && \
    apt-get clean

# 환경 변수 세팅
ENV CHROME_BIN=/usr/bin/chromium-browser
ENV CHROMEDRIVER_PATH=/usr/bin/chromedriver
ENV PATH="${CHROMEDRIVER_PATH}:${PATH}"

WORKDIR /app

COPY --from=build /app/build/libs/*.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
