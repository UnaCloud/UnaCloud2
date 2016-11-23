#!/bin/bash

##############################################
# GRUB PARAMETERS
##############################################

# Load Configuration File
source $CONF_FILE

# Grub default configuration path
# GRUB_CONF_FILE=/etc/default/grub

# Name of the variable for add the new options
# GRUB_OPTIONS=GRUB_CMDLINE_LINUX_DEFAULT

##############################################

# Verifica si el procesador es Intel o AMD
grep -qli "intel\|amd" /proc/cpuinfo
if [ $? == 1 ]
then
  echo "Solamente se soportan procesadores Intel o AMD"
  exit
fi

# Verifica que no haya alguna referencia a IOMMU en la configuracion de GRUB
grep -ql iommu $GRUB_CONF_FILE
if [ $? == 1 ]
then

  echo "Habilitando IOMMU al arranque del sistema"

  # Copia el archivo original
  mv $GRUB_CONF_FILE $GRUB_CONF_FILE\_bk

  # Verifica el tipo de procesador
  grep -qli intel /proc/cpuinfo
  if [ $? == 0 ]
  then
    VENDOR=intel
  else
    VENDOR=amd
  fi

  # Modifica el archivo de GRUB
  sed -e "/$GRUB_OPTIONS/s/\"/ $VENDOR\_iommu\=on\"/2" $GRUB_CONF_FILE\_bk > $GRUB_CONF_FILE

  # Actualiza el GRUB
  if [ $? == 0 ]
  then
    update-grub
  else
    echo "No se pudo actualizar GRUB"
  fi

else
  echo "Ya existe una referencia a IOMMU en el grub, verifique manunalmente"
fi

