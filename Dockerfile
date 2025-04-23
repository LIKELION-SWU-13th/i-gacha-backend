# 🔨 Build Stage
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew bootJar --no-daemon

# 🏃 Runtime Stage (Ubuntu + JRE + Chrome)
FROM ubuntu:22.04
WORKDIR /app

# Java + 크롬 + 크롬드라이버 설치
RUN apt-get update && apt-get install -y \
    openjdk-17-jre-headless \
    wget curl unzip gnupg2 ca-certificates \
    fonts-liberation libappindicator3-1 libasound2 libatk-bridge2.0-0 libatk1.0-0 libgbm1 \
    libnspr4 libnss3 libx11-xcb1 libxcomposite1 libxdamage1 libxrandr2 libxss1 libxtst6 \
    lsb-release xdg-utils chromium-browser chromium-driver && \
    apt-get clean

# 환경 변수 설정
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH="$JAVA_HOME/bin:$PATH"
ENV CHROME_BIN=/usr/bin/chromium-browser
ENV CHROMEDRIVER_PATH=/usr/bin/chromedriver

# JAR 복사
COPY --from=build /app/build/libs/*.jar ./app.jar

EXPOSE 8080


ENTRYPOINT ["sh", "-c", "java -jar app.jar || tail -f /dev/null"]

