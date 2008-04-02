#
# No longer this in the first line: !/bin/bash
###################################################
# SAMPLE SCRIPT FOR MacOS"  [BETA J2SE version]
#  Based on info in https://sourceforge.net/forum/message.php?msg_id=4571837
###################################################

ROOT_DIR=`dirname $0`
if [ -z "$ROOT_DIR" ] ; then ROOT_DIR="." ; fi

#Change to appropriate directory (suppose that we are already in it 
# (or that ROOT_DIR is ok) -> commented)
#cd /Applications/gps ; ROOT_DIR=$PWD*

RXTX_PATH=${ROOT_DIR}/lib/rxtx-2.1-7-bins-r2
RXTX_BIN_PATH=${RXTX_BIN_PATH}/Mac_OS_X

CLASSPATH=${RXTX_PATH}:${RXTX_BIN_PATH}:${ROOT_DIR}/dist/BT747_j2se.jar:.:$CLASSPATH
export CLASSPATH

which java 2>1 >/dev/null && JAVA=java
which javaw 2>1 >/dev/null && JAVA=javaw

# you may want to force the path to the settings file in the next call:
# -Dbt747_settings="${USER}/bt747_settings.pdb"
$JAVA  -Djava.library.path=${RXTX_BIN_PATH} bt747.j2se_view.BT747_main

