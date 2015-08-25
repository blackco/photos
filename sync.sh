#!/bin/sh

echo "hello world"

export ORIGINAL=/Users/blackco/Documents/java/src/photos/src
export NEW=/Users/blackco/Documents/photos/src
export SPRING=java/blackco/photos/spring
export APP=java/blackco/photos/apps

echo ${ORIGINAL}
echo ${NEW}

cp  ${ORIGNAL}/pom.xml ${NEW}/pom.xml
cp  ${ORIGINAL}/main/${SPRING}/*.java ${NEW}/main/${SPRING}
cp  ${ORIGINAL}/test/${APP}/*.java ${NEW}/test/${APP}

rm ${NEW}/main/${SPRING}/Application.java
