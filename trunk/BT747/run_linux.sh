#!/bin/sh
export CLASSPATH
CLASSPATH=webstart/comm.jar:webstart/RXTXcomm.jar:webstart/Waba_only.jar:dist/BT747_rxtx.jar:.:$CLASSPATH

#strace -e trace=file -f -o trace.log
java -Djava.library.path=${PWD}/Linux  waba.applet.Applet /w 320 /h 320 /scale 1 /bpp 8 BT747
