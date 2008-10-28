SETLOCAL
SET DT=1.59.1
SET APP_LANG=de
SET PACK_DIR=pack
dos2unix *.sh
dos2unix *.command
unix2dos *.bat

SET FILES=
set APP_LANG=de
if %APP_LANG%==en ..\uploadBT747.bat dist/BT747_j2se.jar Latest/BT747_j2se.jar
ENDLOCAL
