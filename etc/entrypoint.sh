#!/bin/sh
java -Dlogback.configurationFile=/root/gelf.xml -jar /root/pirx.jar --kafka=$Kafka_Broker --statsDHost=$StatsD_Host