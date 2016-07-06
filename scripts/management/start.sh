#!/bin/sh
export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64
export CATALINA_HOME=/opt/tomcat
export UNACLOUD_HOME=/opt/unacloud
$CATALINA_HOME/bin/startup.sh
nohup java -jar $UNACLOUD_HOME/CloudControl.jar &
