FROM openjdk:jdk-alpine

ENV SCALA_VERSION 2.12.4
ENV KAFKA_BROKER localhost:9092
ENV STATSD_HOST none
ENV DATASETS_PATH /data

WORKDIR /root
ADD target/scala-2.12/pirx.jar /root/pirx.jar
ADD etc/gelf.xml /root/gelf.xml
ADD etc/entrypoint.sh /root/entrypoint.sh
ENTRYPOINT ["/bin/sh","/root/entrypoint.sh"]