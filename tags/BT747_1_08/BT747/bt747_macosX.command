#!/bin/bash
###################################################
# SAMPLE SCRIPT FOR MacOS"
#  Based on info in https://sourceforge.net/forum/message.php?msg_id=4571837
###################################################

#Change to appropriate directory (suppose that we are already in it -> commented)
#cd /Applications/gps
ROOT_DIR=$PWD
CLASSPATH=${ROOT_DIR}/rxtx/RXTXcomm.jar:${ROOT_DIR}/webstart/Waba_only.jar:${ROOT_DIR}/BT747_rxtx.jar:.:$CLASSPATH
export CLASSPATH
java -Djava.library.path=${ROOT_DIR}/rxtx/ -Dbt747_port="/dev/cu.SLAB_USBtoUART"
waba.applet.Applet /w 400 /h 400 /scale 1 /bpp 8 BT747

