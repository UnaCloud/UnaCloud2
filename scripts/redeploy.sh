#!/bin/sh
pkill -f 'java -jar'
export PATH_CONFIG=""
export JAVA_HOME=/usr/lib/jvm/default-java
export CATALINA_HOME=/opt/tomcat
export UNACLOUD_HOME=/opt/unacloud
$CATALINA_HOME/bin/shutdown.sh
killall java
rm -rf $CATALINA_HOME/webapps/UnaCloud
rm -rf $CATALINA_HOME/webapps/FileManager
rm $CATALINA_HOME/webapps/UnaCloud.war
rm $CATALINA_HOME/webapps/FileManager.war
cp -f ${PATH_CONFIG}UnaCloud.war $CATALINA_HOME/webapps/
cp -f ${PATH_CONFIG}FileManager.war $CATALINA_HOME/webapps/
cp -f ${PATH_CONFIG}CloudControl.jar $UNACLOUD_HOME/
$CATALINA_HOME/bin/startup.sh
nohup java -jar CloudControl.jar  &
