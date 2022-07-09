FROM azul/zulu-openjdk-alpine:18 AS builder

WORKDIR /hirobot

COPY gradle ./gradle
COPY gradlew build.gradle.kts settings.gradle.kts ./
# RUN ./gradlew --no-daemon dependencies
COPY . .
RUN ./gradlew --no-daemon build
# RUN ./gradlew build

FROM azul/zulu-openjdk-alpine:18-jre

WORKDIR /hirobot
COPY --from=builder /hirobot/build/libs/HiroBot*-all.jar ./hirobot.jar

CMD ["java", "-jar", "hirobot.jar"]
