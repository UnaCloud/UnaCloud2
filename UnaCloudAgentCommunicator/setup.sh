#!/bin/bash

UNACLOUD_CONF=unacloud.conf
RUNFILE_PATH=run.sh

source $UNACLOUD_CONF

# Install dependencies
apt-get update; apt-get install -y gcc g++ python python-dev python-virtualenv

virtualenv $VIRTUALENV_PATH
cp $UNACLOUD_CONF $VIRTUALENV_PATH/
cp $REQUIREMENTS_FILE $VIRTUALENV_PATH/
cp $UNACLOUD_AGENT_COMM $VIRTUALENV_PATH/

cd $VIRTUALENV_PATH
source bin/activate
pip install --upgrade pip
pip install -r $REQUIREMENTS_FILE

RUNFILE=$(cat <<EOF
#!/bin/bash

UNACLOUD_CONF=$(pwd)/$UNACLOUD_CONF

source \$UNACLOUD_CONF

cd \$VIRTUALENV_PATH
source bin/activate

until ping -c3 \$HOST_IP_ADDRESS
do
	# Wait for network 
	sleep 5s
done

python \$UNACLOUD_AGENT_COMM \$INTERFACE_NAME \$HOST_IP_ADDRESS \$HOST_SOCKET
EOF
)

echo "$RUNFILE" > $RUNFILE_PATH
chmod u+x $RUNFILE_PATH
