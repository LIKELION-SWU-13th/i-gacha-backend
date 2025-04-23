# 🔨 Build Stage
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# 프로젝트 복사
COPY . .

# ✅ gradlew 실행 권한 부여
RUN chmod +x ./gradlew

# 의존성 설치 및 빌드
RUN ./gradlew bootJar --no-daemon


# 🏃 Runtime Stage
FROM ubuntu:22.04
WORKDIR /app

# ✅ 필요한 패키지 및 Chrome + Chromedriver 설치
RUN apt-get update && apt-get install -y \
  wget curl unzip gnupg2 ca-certificates \
  fonts-liberation libappindicator3-1 libasound2 libatk-bridge2.0-0 libatk1.0-0 libgbm1 \
  libnspr4 libnss3 libx11-xcb1 libxcomposite1 libxdamage1 libxrandr2 libxss1 libxtst6 \
  lsb-release xdg-utils chromium-browser chromium-driver && \
  apt-get clean

# ✅ 환경 변수 설정
ENV CHROME_BIN=/usr/bin/chromium-browser
ENV CHROMEDRIVER_PATH=/usr/bin/chromedriver
ENV PATH="${CHROMEDRIVER_PATH}:${PATH}"

# JAR 복사
COPY --from=build /app/build/libs/*.jar ./app.jar

EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
