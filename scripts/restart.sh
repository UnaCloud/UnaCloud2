#!/bin/sh
export PATH_CONFIG=""
export JAVA_HOME=/usr/lib/jvm/java-8-oracle
export CATALINA_HOME=/opt/tomcat
export UNACLOUD_HOME=/opt/unacloud
$CATALINA_HOME/bin/shutdown.sh
pkill -f 'java -jar'
killall java
rm -f /opt/tomcat/logs/*
$CATALINA_HOME/bin/startup.sh
cd $UNACLOUD_HOME/
nohup java -jar CloudControl.jar  &
