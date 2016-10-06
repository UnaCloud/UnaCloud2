#!/usr/bin/python

import socket, netifaces, sys

# Interface name
iface_name = sys.argv[1]

# Host IP Address
host_ip = sys.argv[2]

# Host Port
host_socket = sys.argv[3]

nat_addr = netifaces.ifaddresses(iface_name)[netifaces.AF_INET][0]['addr']

agent_socket = socket.socket()
agent_socket.connect((host_ip, int(host_socket)))

message = nat_addr

agent_socket.sendall(message)

