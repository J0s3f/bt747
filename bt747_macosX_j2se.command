#!/bin/bash
# The above line was removed once, but it was suggested to put it back in.
# Failing to find why it was removed, I did so, please tell me if you know
# why it should be there.
###################################################
# SAMPLE SCRIPT FOR MacOS"  [BETA J2SE version]
#  Based on info in https://sourceforge.net/forum/message.php?msg_id=4571837
###################################################

ROOT_DIR=`dirname "$0"`
if [ -z "$ROOT_DIR" ] ; then ROOT_DIR="." ; fi

#Change to appropriate directory (suppose that we are already in it 
# (or that ROOT_DIR is ok) -> commented)
#cd /Applications/gps ; ROOT_DIR=$PWD*


# Determine the OS VERSION and convert it to an integer
# so that we can test it in shell (major_minor = major*100 + minor)
os_version=$(sw_vers  -productVersion)
minor=$(echo $os_version | sed -e 's/^[^.]*\.//' -e 's/\..*//')
major_minor=$(( ${os_version/.*/} * 100 + ${minor} ))
if (( $major_minor >= 1006 )) ; then
 # USE THE NEWER RXTX CODE (BETA)
 RXTX_PATH="${ROOT_DIR}/lib/rxtx-2.2pre2-bins"
 RXTX_BIN_PATH="${RXTX_PATH}/Mac_OS_X/mac-10.5"
else
 # USE THE OLDER RXTX CODE
 RXTX_PATH="${ROOT_DIR}/lib/rxtx-2.1-7-bins-r2"
 RXTX_BIN_PATH="${RXTX_PATH}/Mac_OS_X"
fi

CLASSPATH="${RXTX_PATH}:${RXTX_BIN_PATH}:${ROOT_DIR}/dist/BT747_j2se.jar:${ROOT_DIR}/lib/swing-layout-1.0.3.jar:${ROOT_DIR}/lib/jcalendar-1.3.2.jar:.:$CLASSPATH"
export CLASSPATH

MEM_OPTION=-Xmx192m

which java >/dev/null 2>&1 && JAVA=java
which javaw >/dev/null 2>&1 && JAVA=javaw

# you may want to force the path to the settings file in the next call:
# -Dbt747_settings="${USER}/bt747_settings.pdb"
#$JAVA ${MEM_OPTION}  -Djava.library.path="${RXTX_BIN_PATH}" bt747.j2se_view.BT747Main
$JAVA  ${MEM_OPTION} -Djava.library.path="${RXTX_BIN_PATH}" bt747.j2se_view.BT747Main $*
