#!/bin/bash

# SET THE UNACLOUD CONFIGURATION FILE PATH
CONFIG_PATH='Configuration File Path'
CONFIG_FILE="$CONFIG_PATH/config.properties"

source 'get_var_val.sh'

# SET CONFIGURATION VARIABLES ######### 
echo 'Please type the MySql Administrator Password:'
read -s MYSQL_ADMIN_PASS
MYSQL_USER=$(get_var_value DB_USERNAME)
MYSQL_PASS=$(get_var_value DB_PASS)
MYSQL_DB=$(get_var_value DB_NAME)
####################################### 

# UPDATE OS REPOSITORY LIST ########### 
apt-get update
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
