#!/bin/bash

##############################################
# LIBVIRT INSTALL PARAMETERS
##############################################

source $CONF_FILE

# Repository list file
# APT_SOURCE_FILE='/etc/apt/sources.list'

##############################################

#####################################
# Add a line with a comment to a file
# $1 exit from last command (grep)
# $2 file
# $3 comment
# $4 line content
function add_line {

  if [ $1 -eq 1 ]
  then
    echo '' >> $2
    echo "# $3" >> $2
    echo "$4" >> $2
  fi
}
#####################################

# Add backports debian repository #####################################
grep -lq -e '[ ]*deb http[^ ]* jessie-backports main.*$' $APT_SOURCE_FILE
add_line $? $APT_SOURCE_FILE 'Debian backports repository for install qemu and libvirt new versions' 'deb http://ftp.us.debian.org/debian/ jessie-backports main'

grep -lq -e '[ ]*deb-src http[^ ]* jessie-backports main.*$' $APT_SOURCE_FILE
add_line $? $APT_SOURCE_FILE 'Debian backports sources repository for install qemu and libvirt new versions' 'deb-src http://ftp.us.debian.org/debian/ jessie-backports main'
##########################################################################

# Actualiza el repositorio y el sistema
apt-get update && apt-get upgrade -y

# Instala los paquetes de virtualizacion necesarios
apt-get install -y gcc g++ dkms linux-headers-$(uname -r) uuid-runtime ssh tightvncserver

# Instala qemu y libvirt desde los backports
apt-get -t jessie-backports install -y libvirt-bin virtinst qemu
