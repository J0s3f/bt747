#!/bin/sh
MYROOTPATH=.
RXTXPATH=${MYROOTPATH}/rxtx-2.1-7-bins-r2
RXTXLIBPATH=${MYROOTPATH}/rxtx-2.1-7-bins-r2/Linux/i686-unknown-linux-gnu
export CLASSPATH
CLASSPATH=${RXTXPATH}/RXTXcomm.jar:webstart/Waba_only.jar:Linux/BT747.jar:.:$CLASSPATH

# Change the port prefix by adding the following option to the java invocation:
#     (the example is for ports like /dev/ttyUSB0)
#  -Dbt747_prefix="/dev/ttyUSB" 


#strace -e trace=file -f -o trace.log
java -Djava.library.path=${RXTXLIBPATH}  waba.applet.Applet BT747
