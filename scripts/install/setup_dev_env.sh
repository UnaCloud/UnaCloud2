#!/bin/bash

# SET THE UNACLOUD CONFIGURATION FILE PATH
CONFIG_PATH='Configuration File Path'
CONFIG_FILE="$CONFIG_PATH/config.properties"

source 'get_var_val.sh'

# SET CONFIGURATION VARIABLES ######### 
echo 'Please type the MySql Administrator Password:'
read -s MYSQL_ADMIN_PASS
MYSQL_USER=$(get_var_value DEV_USERNAME)
MYSQL_PASS=$(get_var_value DEV_PASSWORD)
MYSQL_DB=$(echo $(get_var_value DEV_URL) | sed -e 's/^jdbc:mysql:\/\/[^\/]*\/\([^?]*\).*$/\1/')
####################################### 

# Create and configure UnaCloud User and Database
mysql -u root -p$MYSQL_ADMIN_PASS -e "CREATE DATABASE $MYSQL_DB;"
mysql -u root -p$MYSQL_ADMIN_PASS -e "CREATE USER '$MYSQL_USER'@'localhost' IDENTIFIED BY '$MYSQL_PASS'"
mysql -u root -p$MYSQL_ADMIN_PASS -e "GRANT ALL PRIVILEGES ON $MYSQL_DB.* TO '$MYSQL_USER'@'localhost';"
####################################### 
