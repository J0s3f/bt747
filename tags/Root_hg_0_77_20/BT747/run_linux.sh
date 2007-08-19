#!/bin/sh
export CLASSPATH
CLASSPATH=webstart/comm.jar:webstart/RXTXcomm.jar:webstart/Waba_only.jar:build/BT747.jar:.:$CLASSPATH

cp Linux/javax.comm.properties webstart/
cp Linux/swserial.properties ./
#strace -e trace=file -f -o trace.log
java -Djava.library.path=${PWD}/Linux  waba.applet.Applet BT747
