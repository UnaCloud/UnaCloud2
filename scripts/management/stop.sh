#!/bin/sh
pkill -f 'java -jar'
export JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64
export CATALINA_HOME=/opt/tomcat
$CATALINA_HOME/bin/shutdown.sh
killall java
