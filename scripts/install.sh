#!/bin/sh
export PATH_CONFIG=""
RABBIT_USER=$(grep -w "QUEUE_USER" ${PATH_CONFIG}config.properties  | cut -c12-)
RABBIT_USER=`echo $RABBIT_USER | tr -d '\r'`
RABBIT_PASS=$(grep -w "QUEUE_PASS" ${PATH_CONFIG}config.properties  | cut -c12-)
RABBIT_PASS=`echo $RABBIT_PASS | tr -d '\r'`
MYSQL_USER=$(grep -w "DB_USERNAME" ${PATH_CONFIG}config.properties  | cut -c12-)
MYSQL_USER=`echo $MYSQL_USER | tr -d '\r'`
MYSQL_PASS=$(grep -w "DB_PASS" ${PATH_CONFIG}config.properties  | cut -c12-)
MYSQL_PASS=`echo $MYSQL_PASS | tr -d '\r'`
MYSQL_DB=$(grep -w "DB_NAME" ${PATH_CONFIG}config.properties  | cut -c12-)
MYSQL_DB=`echo $MYSQL_DB | tr -d '\r'`

apt-get update

#Rabbit
apt-get install rabbitmq-server
/usr/lib/rabbitmq/bin/rabbitmq-plugins enable rabbitmq_management
rabbitmqctl add_user $RABBIT_USER $RABBIT_PASS
rabbitmqctl set_user_tags $RABBIT_USER administrator
rabbitmqctl set_permissions -p / $RABBIT_USER ".*" ".*" ".*"
rabbitmqctl delete_user guest
rabbitmqctl stop
invoke-rc.d rabbitmq-server start

#Tomcat
apt-get update
apt-get install openjdk-7-jdk
apt-get install default-jdk
mkdir /opt/tomcat
wget http://www-us.apache.org/dist/tomcat/tomcat-8/v8.0.33/bin/apache-tomcat-8.0.33.tar.gz
tar xvf apache-tomcat-8*tar.gz -C /opt/tomcat --strip-components=1

#mysql
debconf-set-selections <<< 'mysql-server mysql-server/root_password password '$MYSQL_PASS
debconf-set-selections <<< 'mysql-server mysql-server/root_password_again password '$MYSQL_PASS
apt-get install mysql-server
mysql --user=$MYSQL_USER --password=$MYSQL_PASS --execute="CREATE DATABASE ${MYSQL_DB};"

#UnaCloud
mkdir /opt/unacloud
mkdir /opt/unacloud/repo
export UNACLOUD_HOME=/opt/unacloud
unset RABBIT_USER
unset RABBIT_PASS
unset MYSQL_USER
unset MYSQL_PASS
unset MYSQL_DB

cp -f ${PATH_CONFIG}UnaCloud.war $CATALINA_HOME/webapps/
cp -f ${PATH_CONFIG}FileManager.war $CATALINA_HOME/webapps/
cp -f ${PATH_CONFIG}CloudControl.jar $UNACLOUD_HOME/

