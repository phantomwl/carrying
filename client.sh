#!/bin/sh

java -Xmx2048m -Xms2048m -jar ./carrying-client/target/carrying-client-jar-with-dependencies.jar ${1} ${2}
