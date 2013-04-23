#!/bin/sh

if [ -z "$SBT_OPTS" ]; then
        SBT_OPTS="-Xmx4096m -Xms4096m -XX:NewSize=768m -XX:MaxPermSize=1024m";
fi
java ${SBT_OPTS} -Dactors.minPoolSize=128 -Dactors.corePoolSize=256 -Dactors.maxPoolSize=512 -jar `dirname $0`/lib/sbt-launch.jar "$@"
