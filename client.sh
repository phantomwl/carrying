#!/bin/sh

java -Xmx2G -Xms2G -Xmn1G -jar ./carrying-client/target/carrying-client-jar-with-dependencies.jar ${1} ${2} ./carrying-client/carrying-client.properties

