#!/bin/bash

# Working directory
WORKING_DIR=.

# Log file
LOG_FILE=$WORKING_DIR/conf.log
ERROR_LOG_FILE=$WORKING_DIR/conf_error.log

# Installation scripts directory
SCRIPTS_DIR=$WORKING_DIR/inst_scripts

# Export configuration file
export CONF_FILE=$WORKING_DIR/install.conf

# Write a text in all the logs
function write_to_logs {

	echo "$1" >> $LOG_FILE
	echo "$1" >> $ERROR_LOG_FILE
}

write_to_logs "$(date +%F-%X): BEGINNING IOMMU SETUP"

# Configure grub
write_to_logs "$(date +%F-%X): Configuring GRUB"
source $SCRIPTS_DIR/cgrub.sh >> $LOG_FILE 2>> $ERROR_LOG_FILE

# Install libvirt
write_to_logs "$(date +%F-%X): Installing Libvirt"
source $SCRIPTS_DIR/install_libvirt.sh >> $LOG_FILE 2>> $ERROR_LOG_FILE

# Configure libvirt
write_to_logs "$(date +%F-%X): Configuring Libvirt"
source $SCRIPTS_DIR/config_libvirt.sh >> $LOG_FILE 2>> $ERROR_LOG_FILE

write_to_logs "$(date +%F-%X): DONE"
