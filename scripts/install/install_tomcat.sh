#!/bin/bash

# SET CONFIGURATION VARIABLES ######### 
TOMCAT_TARBALL_URL=http://archive.apache.org/dist/tomcat/tomcat-8/v8.0.33/bin/apache-tomcat-8.0.33.tar.gz
TOMCAT_TARBALL=tomcat.tar.gz
TOMCAT_HOME=/opt/tomcat
####################################### 

# UPDATE OS REPOSITORY LIST ########### 
apt-get update
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
