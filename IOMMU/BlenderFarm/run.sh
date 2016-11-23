#!/bin/bash

ssh unacloud@157.253.201.191 "time blender -b /media/blender/bmps_27.blend -P /media/blender/cpu.py 1 25 > /media/blender/time191_256_cpu.txt" &
ssh unacloud@157.253.201.192 "time blender -b /media/blender/bmps_27.blend -P /media/blender/cpu.py 26 50 > /media/blender/time192_256_cpu.txt" &
ssh unacloud@157.253.201.193 "time blender -b /media/blender/bmps_27.blend -P /media/blender/cpu.py 51 75 > /media/blender/time193_256_cpu.txt" &
ssh unacloud@157.253.201.195 "time blender -b /media/blender/bmps_27.blend -P /media/blender/cpu.py 76 100 > /media/blender/time195_256_cpu.txt" &
