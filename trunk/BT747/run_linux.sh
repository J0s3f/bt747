#!/bin/sh
CLASSPATH=lib/comm.jar:lib/RXTXcomm.jar:lib/Waba_only.jar:dist/BT747_rxtx.jar:.:$CLASSPATH
export CLASSPATH

#strace -e trace=file -f -o trace.log
#java -Djava.library.path=${PWD}/Linux 
java  waba.applet.Applet /w 320 /h 320 /scale 1 /bpp 8 BT747
