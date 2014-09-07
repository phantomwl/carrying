#!/bin/sh

java -Xmx4G -Xms4G -Xmn2G -jar ./carrying-client/target/carrying-client-jar-with-dependencies.jar ${1} ${2}
