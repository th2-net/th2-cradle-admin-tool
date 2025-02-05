FROM gradle:8.11.1-jdk11 AS build
ARG Prelease_version=0.0.0
COPY ./ .
RUN gradle clean build dockerPrepare -p cradle-admin-tool-http -Prelease_version=${Prelease_version}

FROM adoptopenjdk/openjdk11:alpine
RUN apk add bash jq curl grep # These utils are required for running embedded scripts
WORKDIR /home
COPY --from=build /home/gradle/cradle-admin-tool-http/build/docker .
ENTRYPOINT ["/home/service/bin/service", "run", "com.exactpro.th2.cradle.adm.http.Application"]
