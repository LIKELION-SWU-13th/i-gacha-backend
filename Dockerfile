#FROM eclipse-temurin:17-jdk

#RUN apt-get update && \
#    apt-get install -y wget gnupg2 curl unzip \
#    libglib2.0-0 libnss3 libatk1.0-0 libatk-bridge2.0-0 \
#    libcups2 libxcomposite1 libxrandr2 libxdamage1 \
#    libxext6 libxfixes3 libx11-xcb1 libx11-6 \
#    libpangocairo-1.0-0 libgtk-3-0 libxshmfence1 \
#    libgbm1 libxrender1 libasound2-plugins && \
#    rm -rf /var/lib/apt/lists/*

#WORKDIR /app

#COPY . .

# gradlew 실행 권한 부여
#RUN chmod +x gradlew

#RUN ./gradlew build -x test

#CMD ["java", "-jar", "build/libs/backend-0.0.1-SNAPSHOT.jar"]

FROM gradle:8.0-jdk17 AS build
COPY . /app
WORKDIR /app
RUN gradle bootJar

FROM openjdk:17-jdk-slim
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
