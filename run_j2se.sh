#!/bin/sh
# You may need to change the next line to an absolute path.
ROOT_DIR=`dirname $0`
if [ -z "$ROOT_DIR" ] ; then ROOT_DIR="." ; fi

if [ -e /usr/share/java/RXTXcomm.jar ] ; then
 # if librxtx-java seems to be installed locally (e.g., on Ubuntu)
 RXTXPATH=/usr/lib
 RXTXLIBPATH=/usr/lib
 RXTXJAR=/usr/share/java/RXTXcomm.jar
else
 RXTXPATH=${ROOT_DIR}/lib/rxtx-2.1-7-bins-r2
 RXTXLIBPATH=${RXTXPATH}/Linux/i686-unknown-linux-gnu
 ARCH=`arch`
 TMPRXTXPATH=${RXTXPATH}/Linux/${ARCH}-unknown-linux-gnu
 if [ -r ${TMPRXTXPATH} ] ; then
   RXTXLIBPATH=${TMPRXTXPATH}
 fi
 RXTXJAR=${RXTXPATH}/RXTXcomm.jar
fi

export CLASSPATH
CLASSPATH=${ROOT_DIR}/lib/jcalendar-1.3.2.jar:$CLASSPATH
CLASSPATH=${ROOT_DIR}/lib/swing-layout-1.0.3.jar:$CLASSPATH
CLASSPATH=${RXTXJAR}:${ROOT_DIR}/dist/libBT747.jar:$CLASSPATH

# Change the port prefix by adding the following option to the java invocation:
#     (the example is for ports like /dev/ttyUSB0)
#  -Dbt747_prefix="/dev/ttyUSB" 
#
# It is possible to define the path to the configuration file
#       -Dbt747_settings="bt747settings.pdb"

which java >/dev/null 2>&1 && JAVA=java
which javaw >/dev/null 2>&1 && JAVA=javaw

#strace -e trace=file -f -o trace.log
$JAVA -Djava.library.path=${RXTXLIBPATH} -jar ${ROOT_DIR}/dist/BT747_j2se.jar &
