FROM gradle:8.11.1-jdk11 AS build
ARG Prelease_version=0.0.0
COPY ./ .
RUN gradle clean build dockerPrepare -p cradle-admin-tool-http -Prelease_version=${Prelease_version}

FROM adoptopenjdk/openjdk11:alpine
RUN apk add bash jq curl grep # These utils are required for running embedded scripts
ENV CRADLE_INSTANCE_NAME=instance1 \
    CASSANDRA_DATA_CENTER=kos \
    CASSANDRA_HOST=cassandra \
    CASSANDRA_PORT=9042 \
    CASSANDRA_KEYSPACE=demo \
    CASSANDRA_USERNAME=guest \
    CASSANDRA_PASSWORD=guest \
    HTTP_PORT=8080 \
    HTTP_HOST=localhost
WORKDIR /home
COPY --from=build /home/gradle/cradle-admin-tool-http/build/docker .
ENTRYPOINT ["/home/service/bin/service", "run", "com.exactpro.th2.cradle.adm.http.Application"]
