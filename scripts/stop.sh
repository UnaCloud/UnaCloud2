#!/bin/sh
pkill -f 'java -jar'
export JAVA_HOME=/usr/lib/jvm/default-java
export CATALINA_HOME=/opt/tomcat
$CATALINA_HOME/bin/shutdown.sh
killall java
