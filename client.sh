#!/bin/sh

java -Xmx8G -Xms8G -Xmn4G -jar ./carrying-client/target/carrying-client-jar-with-dependencies.jar ${1} ${2}
