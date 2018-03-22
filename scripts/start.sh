#!/bin/sh
export JAVA_HOME=/usr/lib/jvm/default-java
export CATALINA_HOME=/opt/tomcat
export UNACLOUD_HOME=/opt/unacloud
$CATALINA_HOME/bin/startup.sh
nohup java -jar CloudControl.jar &
