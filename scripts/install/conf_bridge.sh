#!/bin/bash
# All these commands need Super User Privileges and will set Offline the Machine
INTERFACES_CONF=/etc/network/interfaces
HOST_IFACE=eth0
BRIDGE_IFACE=unacloud-br0
SYSCTL_CONF=/etc/sysctl.conf
IPTABLES_CONF=/etc/iptables/rules.v4

# Instal dependencies
apt-get update; apt-get install bridge-utils iptables-persistent

# Disable release host ip address
ifconfig $HOST_IFACE 0.0.0.0 && ifconfig $HOST_IFACE down

# Configure bridge on interfaces configuration file
BRIDGE_CONF=$(cat <<EOF
# Bridge connection
auto $BRIDGE_IFACE
iface $BRIDGE_IFACE inet dhcp
	bridge_ports $HOST_IFACE
EOF
)

echo "$BRIDGE_CONF" >> $INTERFACES_CONF

# Enable the bridge interface
service networking restart

# Configure iptables for accept bridge forwarding
iptables -A FORWARD -m physdev --physdev-is-bridge -j ACCEPT
iptables-save > $IPTABLES_CONF

# Configure kernel options for not use iptables rules on bridge packages
SYSCTL_OPTS=$(cat <<EOF
# Bridge IPTABLES Options
net.bridge.bridge-nf-call-iptables=0
net.bridge.bridge-nf-call-ip6tables=0
EOF
)

echo "$SYSCTL_OPTS" >> $SYSCTL_CONF

sysctl -p $SYSCTL_CONF
