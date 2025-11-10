FROM amazoncorretto:11-alpine-jdk
WORKDIR /home
COPY ./cradle-admin-tool-http/build/docker .
ENTRYPOINT ["/home/service/bin/service", "run", "com.exactpro.th2.cradle.adm.http.Application"]