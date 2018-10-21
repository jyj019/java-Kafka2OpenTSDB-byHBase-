#!/bin/bash

unset PROJECT_HOME
export PROJECT_HOME="$(cd "`dirname "$0"`"/..; pwd)"
. ${PROJECT_HOME}/bin/load-env.sh

count=`ps -ef | grep ${CLASSNAME} | grep java | grep -v grep | grep -v  watchdog | wc -l`

timestr=`date`
if [ ${count} -lt 1 ];then
  sh ${PROJECT_HOME}/bin/start.sh
  echo " reboot  at $timestr ..." >> run.log
else
  echo " running ok  at $timestr ..." >> run.log
fi
