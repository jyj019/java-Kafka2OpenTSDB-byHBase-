#!/bin/bash

unset PROJECT_HOME
export PROJECT_HOME="$(cd "`dirname "$0"`"/..; pwd)"
. ${PROJECT_HOME}/bin/load-env.sh

showUsage() {
        echo "Usage: stop.sh or stop.sh -f"
}

if [ $# -gt 1 ]
    then
        showUsage
    exit 0
fi

if [ $# == 1 ]
    then
        if [ "$1" == "-f" ]
        then
        ps -ef|grep ${CLASSNAME} |grep java |grep -v grep |awk '{print  "kill -9 " $2}'|sh
else
        showUsage
exit 0
fi
else
     ps -ef|grep ${CLASSNAME} |grep java |grep -v grep |awk '{print  "kill " $2}'|sh
fi

stopcount=0
while (true)
      do
         count=`ps -ef|grep ${CLASSNAME}|grep java|grep -v grep |wc -l`
        if [ ${count} -lt 1 ]
        then
            echo "stop ok "
        exit 0
        else
            echo "is running,just wait ..."
        sleep 10
        stopcount=$(($stopcount+1))
        fi
done
