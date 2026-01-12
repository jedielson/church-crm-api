FROM maven:3.9.11-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src src

RUN mvn install -Dmaven.test.skip=true

FROM build AS test

CMD ["clean", "verify"]
ENTRYPOINT ["mvn"]

FROM build AS publish

WORKDIR /app
RUN mvn deploy -Dmaven.test.skip=true -Pdocker-compose

FROM eclipse-temurin:21 AS final

WORKDIR /app

COPY --from=publish /app/target /app/target/
RUN mv ./target/*-SNAPSHOT.jar service.jar && rm -rf target

ARG COMMIT_SHA
ENV COMMIT_SHA="${COMMIT_SHA}"

ARG _PROJECT_NAME
ENV _PROJECT_NAME="${_PROJECT_NAME}"

ARG _PROJECT_PATH
ENV _PROJECT_PATH="${_PROJECT_PATH}"

ARG _COMMIT_NAME
ENV _COMMIT_NAME="${_COMMIT_NAME}"

ARG _PROJECT_ID
ENV _PROJECT_ID="${_PROJECT_ID}"

ARG _ENVIRONMENT_NAME
ENV ENVIRONMENT_NAME=${_ENVIRONMENT_NAME}

ENTRYPOINT ["sh", "-c", "exec java -jar service.jar"]