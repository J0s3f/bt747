#
# No longer this in the first line: !/bin/bash
###################################################
# SAMPLE SCRIPT FOR MacOS"
#  Based on info in https://sourceforge.net/forum/message.php?msg_id=4571837
###################################################

ROOT_DIR=`dirname $0`
if [ -z "$ROOT_DIR" ] ; then ROOT_DIR="." ; fi

#Change to appropriate directory (suppose that we are already in it 
# (or that ROOT_DIR is ok) -> commented)
#cd /Applications/gps ; ROOT_DIR=$PWD

RXTX_PATH=${ROOT_DIR}/lib/rxtx-2.1-7-bins-r2
RXTX_BIN_PATH=${RXTX_PATH}/Mac_OS_X

CLASSPATH=${RXTX_PATH}:${RXTX_BIN_PATH}:${ROOT_DIR}/lib/Waba_only.jar:${ROOT_DIR}/dist/BT747_rxtx.jar:.:$CLASSPATH
export CLASSPATH
# More advanced way of looking for ports
POSSIBLE_PORTS="$POSSIBLE_PORTS /dev/cu.SLAB_USBtoUART"
POSSIBLE_PORTS="$POSSIBLE_PORTS /dev/tty.HOLUX_M-241-SPPSlave-1"
POSSIBLE_PORTS="$POSSIBLE_PORTS /dev/tty.HOLUX_M-241-SPPSlave-0"
POSSIBLE_PORTS="$POSSIBLE_PORTS /dev/tty.iBT-GPS-SPPSlave-1"
POSSIBLE_PORTS="$POSSIBLE_PORTS /dev/cu.serial-0001"
POSSIBLE_PORTS="$POSSIBLE_PORTS /dev/tty.iBT-GPS-SPPslave-1"

# Default port option
PORT_OPTION="-Dbt747_port=/dev/cu.SLAB_USBtoUART"
for port in  $POSSIBLE_PORTS ; do
  echo Trying port $port
  if [ -r $port ] ; then
    echo Selecting port $port
    PORT_OPTION="-Dbt747_port=$port"
    break  ## Exit for loop - port found
  fi 
done

which java 2>1 >/dev/null && JAVA=java
which javaw 2>1 >/dev/null && JAVA=javaw

# you may want to force the path to the settings file in the next call:
# -Dbt747_settings="${USER}/bt747_settings.pdb"
$JAVA -Djava.library.path=${RXTX_BIN_PATH} $PORT_OPTION waba.applet.Applet /w 400 /h 400 /scale 1 /bpp 8 BT747

