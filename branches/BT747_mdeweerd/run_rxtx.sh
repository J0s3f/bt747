#!/bin/sh
if [ -e /usr/share/java/RXTXcomm.jar ] ; then
 # if librxtx-java seems to be installed locally (e.g., on Ubuntu)
 RXTXPATH=/usr/lib
 RXTXLIBPATH=/usr/lib
 RXTXJAR=/usr/share/java/RXTXcomm.jar
else
 # You may need to change the next line to an absolute path.
 MYROOTPATH=.
 RXTXPATH=${MYROOTPATH}/lib/rxtx-2.1-7-bins-r2
 RXTXLIBPATH=${RXTXPATH}/Linux/i686-unknown-linux-gnu
 ARCH=`arch`
 TMPRXTXPATH=${RXTXPATH}/Linux/${ARCH}-unknown-linux-gnu
 if [ -r ${TMPRXTXPATH} ] ; then
   RXTXLIBPATH=${TMPRXTXPATH}
 fi
 RXTXJAR=${RXTXPATH}/RXTXcomm.jar
fi

export CLASSPATH
CLASSPATH=${RXTXJAR}:lib/Waba_only.jar:dist/BT747_rxtx.jar:.:$CLASSPATH

# Change the port prefix by adding the following option to the java invocation:
#     (the example is for ports like /dev/ttyUSB0)
#  -Dbt747_prefix="/dev/ttyUSB" 
#
# It is possible to define the path to the configuration file
#       -Dbt747_settings="bt747settings.pdb"

which java 2>&1 >/dev/null && JAVA=java
which javaw 2>&1 >/dev/null && JAVA=javaw

#strace -e trace=file -f -o trace.log
$JAVA -Djava.library.path=${RXTXLIBPATH}  waba.applet.Applet /w 320 /h 320 /scale 1 /bpp 8 BT747 &
