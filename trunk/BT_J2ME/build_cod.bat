rem /cygdrive/c/Program Files/Research In Motion/BlackBerry JDE 4.5.0/bin
SET BB_DIR="c:\Program Files\Research In Motion\BlackBerry JDE 4.5.0\bin"
copy deployed\BT_J2ME.* %BB_DIR%
set ORG_DIR=%CD%
cd %BB_DIR%
rapc import="..\lib\net_rim_api.jar" codename=BT_J2ME -midlet jad=BT_J2ME.jad BT_J2ME.jar
cd %ORG_DIR%
cp %BB_DIR%\BT_J2ME.cod dist
