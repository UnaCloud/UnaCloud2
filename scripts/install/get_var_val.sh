#!/bin/bash

# Get the value of a variable from a configuration file
function get_var_value {
	grep "^$1" $CONFIG_FILE | sed "s/^$1=\([^ \r\t\n]*\).*$/\1/"
}
