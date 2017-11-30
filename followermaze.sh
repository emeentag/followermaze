#! /bin/bash
export logLevel="info" \
       eventListenerPort=9090 \
       clientListenerPort=9099 \
       totalEvents=100 \
       concurrencyLevel=10 \
       randomSeed=666 \
       timeout=20000 \
       maxEventSourceBatchSize=100 \
       logInterval=1000

( cd $(dirname $0)
time java -server -Xmx1G -jar ./follower-maze-2.0.jar)
