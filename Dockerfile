FROM openjdk:jdk-alpine

ENV SCALA_VERSION 2.12.4
ENV Kafka_Broker localhost:9092
ENV StatsD_Host none

WORKDIR /root
ADD target/scala-2.12/pirx.jar /root/pirx.jar
ADD etc/gelf.xml /root/gelf.xml
ADD etc/entrypoint.sh /root/entrypoint.sh
ENTRYPOINT ["/bin/sh","/root/entrypoint.sh"]