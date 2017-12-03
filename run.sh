#!/bin/bash
export CONCURRENCY_LEVEL=0 \
       LOG_LEVEL="all" \
       REGISTRATION_SERVER_PORT=9099 \
       EVENT_RECEIVER_SERVER_PORT=9090

PROD="--prod"
JAR_PATH=build/libs/soundcloud-all-1.0.jar

if [ \( "$1" = "$PROD" \) -a \( "$#" -ne 0 \) ]
then
  if [ -f "$JAR_PATH" ]
  then
    echo "Application jar file is exist."
  else
    echo "Application jar file is not exist."
    echo "Building application jar file."

    gradle createJar
  fi

  echo "Running application Jar file."

  java -jar "$JAR_PATH"
else
  gradle run
fi