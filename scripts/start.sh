#!/bin/sh
export JAVA_HOME=/usr/lib/jvm/java-8-oracle
export CATALINA_HOME=/opt/tomcat
export UNACLOUD_HOME=/opt/unacloud
$CATALINA_HOME/bin/startup.sh
cd $UNACLOUD_HOME/
nohup java -jar CloudControl.jar &
