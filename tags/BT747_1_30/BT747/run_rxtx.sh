#!/bin/sh -v
if [ -e /usr/share/java/RXTXcomm.jar ] ; then
 # if librxtx-java seems to be installed locally (e.g., on Ubuntu)
 RXTXPATH=/usr/lib
 RXTXLIBPATH=/usr/lib
 RXTXJAR=/usr/share/java/RXTXcomm.jar
else
 # You may need to change the next line to an absolute path.
 MYROOTPATH=.
 RXTXPATH=${MYROOTPATH}/rxtx-2.1-7-bins-r2
 RXTXLIBPATH=${MYROOTPATH}/rxtx-2.1-7-bins-r2/Linux/i686-unknown-linux-gnu
 RXTXJAR=${RXTXPATH}/RXTXcomm.jar
fi

export CLASSPATH
CLASSPATH=${RXTXJAR}:webstart/Waba_only.jar:dist/BT747_rxtx.jar:.:$CLASSPATH

# Change the port prefix by adding the following option to the java invocation:
#     (the example is for ports like /dev/ttyUSB0)
#  -Dbt747_prefix="/dev/ttyUSB" 
#
# It is possible to define the path to the configuration file
#       -Dbt747_settings="bt747settings.pdb"


#strace -e trace=file -f -o trace.log
java -Djava.library.path=${RXTXLIBPATH}  waba.applet.Applet /w 320 /h 320 /scale 1 /bpp 8 BT747
