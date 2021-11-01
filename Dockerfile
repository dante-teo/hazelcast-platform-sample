FROM openjdk:11 AS builder

ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

COPY build.gradle.kts $APP_HOME
COPY settings.gradle.kts $APP_HOME
COPY gradlew $APP_HOME
COPY gradle $APP_HOME/gradle

RUN ./gradlew build || return 0

COPY . .
RUN ./gradlew build

FROM openjdk:11-jre-slim

ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

COPY --from=builder $APP_HOME/build/libs/app.jar .

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]