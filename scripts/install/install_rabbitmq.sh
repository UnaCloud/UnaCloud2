#!/bin/bash

# SET THE UNACLOUD CONFIGURATION FILE PATH
CONFIG_PATH='Configuration File Path'
CONFIG_FILE="$CONFIG_PATH/config.properties"

source 'get_var_val.sh'

# SET CONFIGURATION VARIABLES ######### 
RABBIT_USER=$(get_var_value QUEUE_USER)
RABBIT_PASS=$(get_var_value QUEUE_PASS)
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
