#!/usr/bin/env bash

loadENV(){
        if [ -f $1 ]
        then
                . $1
        fi
}

loadENV /etc/profile
loadENV ~/.bashrc
loadENV ~/.bash_profile

RUNTYPE="pro"
#RUNTYPE="dev"

if [ -z "${PROJECT_HOME}" ]; then
  export PROJECT_HOME="$(cd "`dirname "$0"`"/..; pwd)"
fi
PROJECT_CONF_DIR=${PROJECT_HOME}/conf

for f in ${PROJECT_CONF_DIR}/${RUNTYPE}/*.*; do
        CLASSPATH=${CLASSPATH}:${f};
done

PROJECT_LIB_DIR=${PROJECT_HOME}/lib

for f in ${PROJECT_LIB_DIR}/*.jar; do
        CLASSPATH=${CLASSPATH}:${f};
done

CLASSNAME="com.calabar.dec.rds.loader.main.TSDBDirectLoader"

