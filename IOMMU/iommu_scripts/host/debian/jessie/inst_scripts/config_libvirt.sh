#!/bin/bash

##############################################
# LIBVIRT CONFIGURATION PARAMETERS
##############################################

source $CONF_FILE

# Libvirt configuration file
# LB_CONF_FILE=/etc/libvirt/libvirtd.conf
LB_CONF_VAR=''

# UNIX socket group
LB_GROUP_VAR=unix_sock_group
# LB_GROUP=libvirt

# Libvirt pivileged user
# LB_USER=gpuvirt

# UNIX socket Read and Write permissions
LB_PERMS_VAR=unix_sock_rw_perms
# LB_PERMS=0770

# UNIX socket directory
LB_SOCK_DIR_VAR=unix_sock_dir
# LB_SOCK_DIR=/var/run/libvirt

# Libvirt service name
# LB_SERVICE_NAME=libvirtd

##############################################


# Set a value to a variable
function set_var {

  echo "$LB_CONF_VAR" | sed s'|'.*$1\ =.*'|'$1\ =\ \"$2\"'|'
}

# Comment a variable
function comment_var {

  echo "$LB_CONF_VAR" | sed "s/\(.*$1\ =.*\)/\#\ \1/"
}

# Store libvirt configuration file on a variable
LB_CONF_VAR=$(cat $LB_CONF_FILE)

# Backup the libvirt configuration file
cp $LB_CONF_FILE $LB_CONF_FILE\_bk

# Set Unix Socket Group User
LB_CONF_VAR=$(set_var $LB_GROUP_VAR $LB_GROUP)

# Set Unix Socket RW permissions
LB_CONF_VAR=$(set_var $LB_PERMS_VAR $LB_PERMS)

# Set Unix Socket Directory
LB_CONF_VAR=$(set_var $LB_SOCK_DIR_VAR $LB_SOCK_DIR)

# Save libvirt configuration file
echo "$LB_CONF_VAR" > $LB_CONF_FILE

# Restart libvirt service
service $LB_SERVICE_NAME restart

# Libvirt privileged user
usermod -a -G $LB_GROUP $LB_USER