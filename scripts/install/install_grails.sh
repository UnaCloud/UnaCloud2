#!/bin/bash

# DEFINE VARIABLES #####################
export SDKMAN_DIR=$HOME/.sdkman
SDKMAN_URL=https://get.sdkman.io/
SDKMAN_SETUP=sdkman-setup.sh
GRAILS_VERSION=2.4.3
########################################

# UPDATE REPOSITORY LIST AND INSTALL DEPENDENCIES
echo 'Please type the root user password:'
su - root -c 'apt-get update; apt-get install -y zip unzip curl wget'
########################################

# DOWNLOAD AND INSTALL SDKMAN ##########
wget -O $SDKMAN_SETUP $SDKMAN_URL
chmod u+x $SDKMAN_SETUP
./$SDKMAN_SETUP
rm $SDKMAN_SETUP
########################################

# INSTALL GRAILS #######################
source $SDKMAN_DIR/bin/sdkman-init.sh
sdk install grails $GRAILS_VERSION
########################################
