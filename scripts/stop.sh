#!/bin/sh
pkill -f 'java -jar'
export JAVA_HOME=/usr/lib/jvm/java-8-oracle
export CATALINA_HOME=/opt/tomcat
$CATALINA_HOME/bin/shutdown.sh
killall java
