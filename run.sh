#! /bin/bash
export CONCURRENCY_LEVEL=0 \
       LOG_LEVEL="all" \
       REGISTRATION_SERVER_PORT=9099 \
       EVENT_RECEIVER_SERVER_PORT=9090

gradle run