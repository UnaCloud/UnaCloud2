#!/bin/sh
export PATH_CONFIG=""
export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64
export CATALINA_HOME=/opt/tomcat
export UNACLOUD_HOME=/opt/unacloud
$CATALINA_HOME/bin/shutdown.sh
pkill -f 'java -jar'
killall java
rm -f /opt/tomcat/logs/*
$CATALINA_HOME/bin/startup.sh
nohup java -jar $UNACLOUD_HOME/CloudControl.jar  &
