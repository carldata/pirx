#!/bin/sh
java -Dlogback.configurationFile=/root/gelf.xml -jar /root/pirx.jar --kafka=$KAFKA_BROKER --statsd-host=$STATSD_HOST --dataset-path=$DATASETS_PATH