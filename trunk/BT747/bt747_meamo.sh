#!/bin/sh
#######################################################
# Script for Nokia Maemo.
# From http://talk.maemo.org/showthread.php?t=28099&highlight=bt747&page=2
#   Author:  Koios.
#######################################################
# Can operate in command line mode (add a parameter to the call).
#
# GUI mode not functional yet at the time of creation
#######################################################
BTADDR=00:xx:xx:xx:xx:xx

BTdevice=$(dbus-send --system --type=method_call --print-reply \
--dest=com.nokia.btcond /com/nokia/btcond/request \
 com.nokia.btcond.request.rfcomm_bind string:$BTADDR string:SPP 2>&1 )
if [ $? != 0 ]; then
 BTdevice=$(echo ${BTdevice} | cut -d ' ' -f 8-)
else
 BTdevice=$(echo ${BTdevice} | cut -d ' ' -f 7- | tr -d \")
fi

MYROOTPATH=. 
  
RXTXLIBPATH=/usr/share/java
export CLASSPATH=lib/jchart2d-2.1.0:lib/jopt-simple-2.4.1.jar:/lib/jcalendar-1.3.2.jar:lib/swing-layout-1.0.3.jar:lib/swingx.jar:lib/swingx-ws.jar:dist/BT747.jar:dist/BT747_j2se.jar:rxtx-2.1-7-bins-r2/RXTXcomm.jar

# MEM HEAP OPTION IS NEEDED WHEN LOADING PICTURES - THIS INCREASES AVAILABLE MEMORY 
MEM_HEAP_OPTION=-Xmx192m
java $MEM_HEAP_OPTION -Dgnu.io.rxtx.SerialPorts=${BTdevice} -Dbt747_port=${BTdevice} -Djava.library.path=${RXTXLIBPATH} bt747.j2se_view.BT747Main $* &
