#!/bin/bash
# Install CUDA 5.0 on Debian 8

# User variables
USER_NAME='user'
USER_HOME="/home/$USER_NAME"
USER_BIN="$USER_HOME/bin"
USER_PROFILE="$USER_HOME/.profile"

# Repository list file
APT_SOURCE_FILE='/etc/apt/sources.list'

# Modprobe blacklist file
MOD_BLACKLIST='/etc/modprobe.d/nvidia-blacklist.conf'

# Nvidia driver installer
DRV_INST="$USER_HOME/NVIDIA-Linux-x86_64-304.131.run"

# CUDA Variables
CUDA_INST="$USER_HOME/cuda_5.0.35_linux_64_ubuntu10.04-1.run"
CUDA_PATH='/usr/local/cuda-5.0'

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

# Upgrade packages from repository #####################################
apt-get update && apt-get upgrade -y && apt-get install -y gcc g++ make linux-headers-$(uname -r)
##########################################################################

# Add old debian repository #####################################
grep -lq -e '[ ]*deb http[^ ]* wheezy main.*$' $APT_SOURCE_FILE
add_line $? $APT_SOURCE_FILE 'Debian old stable repository for install gcc 4.6 and g++ 4.6' 'deb http://ftp.us.debian.org/debian/ wheezy main'

grep -lq -e '[ ]*deb-src http[^ ]* wheezy main.*$' $APT_SOURCE_FILE
add_line $? $APT_SOURCE_FILE 'Debian old stable repository sources for install gcc 4.6 and g++ 4.6' 'deb-src http://ftp.us.debian.org/debian/ wheezy main'
##########################################################################

# Install gcc 4.6 and g++ 4.6 #####################################
apt-get update; apt-get install -y gcc-4.6 g++-4.6
##########################################################################

# Nouveau to blacklist #####################################
echo '# Blacklist for NVIDIA' > $MOD_BLACKLIST
echo blacklist nouveau >> $MOD_BLACKLIST
update-initramfs -u -k $(uname -r)
##########################################################################

# Install NVIDIA 304.xx Driver #####################################
$DRV_INST --silent --no-x-check --no-nouveau-check --no-cc-version-check
##########################################################################

# Install CUDA 5.0 #####################################
$CUDA_INST -silent -verbose -override -toolkit -toolkitpath=$CUDA_PATH
##########################################################################

# Create symbolic links #####################################
mkdir -p $USER_BIN
ln -f -s $(whereis gcc-4 | sed 's/.*\ \([^\ ]*\/bin\/[^\ ]*-4.6\).*/\1/') $USER_BIN/gcc
ln -f -s $(whereis g++-4 | sed 's/.*\ \([^\ ]*\/bin\/[^\ ]*-4.6\).*/\1/') $USER_BIN/g++
chown -R $USER_NAME:$USER_NAME $USER_BIN
##########################################################################

# Set Environment Variables #####################################
grep -lq -e "[ ]*PATH=\"$CUDA_PATH/bin:$USER_BIN:\$PATH\"" $USER_PROFILE
add_line $? $USER_PROFILE 'Binaries of gcc and g++' "PATH=\"$CUDA_PATH/bin:$USER_BIN:\$PATH\""

grep -lq -e "[ ]*export LD_LIBRARY_PATH=\"$CUDA_PATH/lib64:$CUDA_PATH/lib\"" $USER_PROFILE
add_line $? $USER_PROFILE 'Shared libraries for CUDA' "export LD_LIBRARY_PATH=\"$CUDA_PATH/lib64:$CUDA_PATH/lib\""
##########################################################################