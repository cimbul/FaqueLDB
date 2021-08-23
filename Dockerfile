# syntax=docker/dockerfile:1

FROM azul/zulu-openjdk-alpine:11 AS builder
WORKDIR /work
COPY . /work/
RUN ./gradlew assembleDist
RUN mkdir -p build/distributions/FaqueLDB && \
    tar -xf build/distributions/FaqueLDB-*.tar --strip-components 1 -C build/distributions/FaqueLDB

FROM azul/zulu-openjdk-alpine:11
WORKDIR /work
COPY --from=builder /work/build/distributions/FaqueLDB/ FaqueLDB/
CMD ["/work/FaqueLDB/bin/FaqueLDB"]
