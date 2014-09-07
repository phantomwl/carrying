#!/bin/bash

java \
    -XX:+UseBiasedLocking\
    -Xmx4G \
    -Xms4G \
    -Xmn2G \
    -jar ./carrying-server/target/carrying-server-jar-with-dependencies.jar ${1} ${2} ./carrying-server/carrying-server.properties