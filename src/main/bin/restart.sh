#!/bin/bash

unset PROJECT_HOME
export PROJECT_HOME="$(cd "`dirname "$0"`"/..; pwd)"
. ${PROJECT_HOME}/bin/load-env.sh

# 查询 APP_NAME 是否正在运行
APP_ID=$(yarn application -list | grep ${APP_NAME} | awk '{print $1}')
if [[ -n ${APP_ID} ]]
then
    echo "The application ${APP_NAME} is running, applicationId is: ${APP_ID}"
    . ${PROJECT_HOME}/bin/stop.sh
fi

. ${PROJECT_HOME}/bin/start.sh
