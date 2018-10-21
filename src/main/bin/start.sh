#!/bin/bash
unset PROJECT_HOME
export PROJECT_HOME="$(cd "`dirname "$0"`"/..; pwd)"
. ${PROJECT_HOME}/bin/load-env.sh

ps -ef | grep ${CLASSNAME} | grep -v grep > /dev/null 2>&1
if [ $? -eq 0 ];then
  echo "program is ruuning!"
  exit 1
fi

export CONFIG_PATH=${PROJECT_HOME}/conf/${RUNTYPE}

nohup java -Xms2000m -Xmx2000m -Xss256k \
           -XX:+PrintGCDateStamps -XX:+PrintGCDetails  \
           -verbose:gc -Xloggc:${PROJECT_HOME}/log/gc.log \
           -XX:+HeapDumpOnOutOfMemoryError \
           -XX:OnOutOfMemoryError="sh $PROJECT_HOME/bin/stop.sh" \
           -DPROJECT_HOME=${PROJECT_HOME} \
           -DCONFIG_PATH=${PROJECT_HOME}/conf/${RUNTYPE} \
           -cp ${CLASSPATH} ${CLASSNAME}  &
#           -cp ${CLASSPATH} ${CLASSNAME} >/dev/null 2>&1 &
