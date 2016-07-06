#!/bin/bash

# SET THE UNACLOUD CONFIGURATION FILE PATH
CONFIG_PATH='Configuration File Path'
CONFIG_FILE="$CONFIG_PATH/config.properties"

source 'get_var_val.sh'

# SET CONFIGURATION VARIABLES ######### 
# RabbitMQ
RABBIT_USER=$(get_var_value QUEUE_USER)
RABBIT_PASS=$(get_var_value QUEUE_PASS)

# Tomcat
TOMCAT_TARBALL_URL=http://archive.apache.org/dist/tomcat/tomcat-8/v8.0.33/bin/apache-tomcat-8.0.33.tar.gz
TOMCAT_TARBALL=tomcat.tar.gz
TOMCAT_HOME=/opt/tomcat

# MySQL
echo 'Please type the MySql Administrator Password:'
read -s MYSQL_ADMIN_PASS
MYSQL_USER=$(get_var_value DB_USERNAME)
MYSQL_PASS=$(get_var_value DB_PASS)
MYSQL_DB=$(get_var_value DB_NAME)
####################################### 

# UPDATE OS REPOSITORY LIST ########### 
apt-get update
####################################### 

# INSTALL RABBITMQ #################### 
# Install rabbitmq-server from os repository
apt-get install -y rabbitmq-server

# Enable rabbitmq management plugin
rabbitmq-plugins enable rabbitmq_management

# Configure rabbitmq user persmissions
rabbitmqctl add_user $RABBIT_USER $RABBIT_PASS
rabbitmqctl set_user_tags $RABBIT_USER administrator
rabbitmqctl set_permissions -p / $RABBIT_USER ".*" ".*" ".*"
rabbitmqctl delete_user guest

# Restart rabbitmq server
service rabbitmq-server restart
####################################### 

# INSTALL TOMCAT ###################### 
# Install tomcat prerequisites
apt-get install -y openjdk-7-jdk

# Download and deploy tomcat from tarball
mkdir $TOMCAT_HOME
wget -O $TOMCAT_TARBALL $TOMCAT_TARBALL_URL
tar xvf $TOMCAT_TARBALL -C $TOMCAT_HOME --strip-components=1
rm $TOMCAT_TARBALL
####################################### 

# INSTALL MYSQL ####################### 
# Configure and install MySQL from OS repository
echo PURGE | debconf-communicate mysql-server
debconf-set-selections <<< 'mysql-server mysql-server/root_password password '$MYSQL_ADMIN_PASS
debconf-set-selections <<< 'mysql-server mysql-server/root_password_again password '$MYSQL_ADMIN_PASS
apt-get install -y mysql-server

# Create and configure UnaCloud User and Database
mysql -u root -p$MYSQL_ADMIN_PASS -e "CREATE DATABASE $MYSQL_DB;"
mysql -u root -p$MYSQL_ADMIN_PASS -e "CREATE USER '$MYSQL_USER'@'localhost' IDENTIFIED BY '$MYSQL_PASS'"
mysql -u root -p$MYSQL_ADMIN_PASS -e "GRANT ALL PRIVILEGES ON $MYSQL_DB.* TO '$MYSQL_USER'@'localhost';"
####################################### 
