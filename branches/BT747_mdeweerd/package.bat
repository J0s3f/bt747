SETLOCAL
SET PATH=%PATH%;C:\CYGWIN64\BIN
SET BASH=C:\CYGWIN64\BIN\bash
SET DT=2.1.11
SET PACK_DIR=pack

REM recode ibmpc..lat1 winfile.txt   # dos2unix
REM recode lat1..ibmpc unixfile.txt  # unix2dosr

REM dos2unix *.sh
REM dos2unix *.command
REM unix2dos *.bat

recode ibmpc..lat1 *.sh *.command
recode lat1..ibmpc *.bat

SET DISTFILES=
SET SRCFILES=
SET COMMONFILES=
SET DISTFILES=
SET PDAFILES=

SET PDAFILES=%PDAFILES% UFFChi_H.pdb UFFChi_L.pdb
SET PDAFILES=%PDAFILES% UFFJap_H.pdb UFFJap_L.pdb
SET PDAFILES=%PDAFILES% UFFKor_H.pdb UFFKor_L.pdb

SET SRCFILES=%SRCFILES% src*  build.xml .project .classpath default.properties nbproject pom.xml 
SET COMMONFILES=%COMMONFILES% ChangeLog.txt COPYING  README.txt  
SET DISTFILES=%DISTFILES% dist
SET COMMONFILES=%COMMONFILES% bt747_macosX_j2se.command bt747_macosX.command bt747_maemo.sh
SET SRCFILES=%SRCFILES% BT747_l.jnlp BT747_lwin.jnlp
SET COMMONFILES=%COMMONFILES% run_ex.bat run_rxtx.bat run_rxtx.sh run_both.bat run_j2se.bat run_j2se64.bat run_j2se.sh BT747cmd.bat
SET RXTXFILES=lib/rxtx-2.1-7-bins-r2 lib/rxtx-2.2pre2-bins
SET COMMONFILES=%COMMONFILES% lib/collections-superwaba.jar
SET COMMONFILES=%COMMONFILES% lib/RXTXcomm.jar
SET COMMONFILES=%COMMONFILES% lib/Waba_only.jar
SET COMMONFILES=%COMMONFILES% lib/jcalendar-1.3.2.jar
SET COMMONFILES=%COMMONFILES% lib/swing-layout-1.0.3.jar
SET COMMONFILES=%COMMONFILES% lib/swingx-ws.jar lib/swingx.jar
SET COMMONFILES=%COMMONFILES% lib/jopt-simple*.jar
SET COMMONFILES=%COMMONFILES% lib/sanselan*or.jar
SET COMMONFILES=%COMMONFILES% lib/jchart*.jar
SET COMMONFILES=%COMMONFILES% lib/win32comm.jar lib/win32/javax.comm.properties
SET COMMONFILES=%COMMONFILES% lib/win32com.dll  lib/comm.jar
SET COMMONFILES=%COMMONFILES% 5SW.pdb
SET COMMONFILES=%COMMONFILES% BT747.exe BT747cmd.exe BT747_64b.exe BT747cmd_64b.exe bt747.cer bt747new.cer INSTALLKEY.bat
SET SRCFILES=%SRCFILES% dev/*
SET EXCLUDEFILES=nbproject/private/\* \*/src/gps/parser/\*
SET EXCLUDEFILES=%EXCLUDEFILES% dev/bt747 

IF EXIST %PACK_DIR%\BT747_%DT%_*.zip del %PACK_DIR%\BT747_%DT%_*.zip
IF NOT EXIST %PACK_DIR% mkdir %PACK_DIR%

SET FILES=%DISTFILES% %COMMONFILES% %PDAFILES%

SET FILES=%COMMONFILES% %SRCFILES% %COMMONFILES% %PDAFILES%
SET FILES=%DISTFILES% %SRCFILES% %COMMONFILES% %DISTFILES% %PDAFILES%
chmod 755 *.command *.sh *.bat *.BAT *.EXE *.exe
zip -q -9 -r %PACK_DIR%/BT747_%DT%_full.zip %FILES% %RXTXFILES% -x \*/.svn/\* src/CVS/\* \*/CVS/\* nbproject/private %EXCLUDEFILES%

CALL ..\uploadBT747.bat dist\libBT747.jar Latest/libBT747.jar
CALL ..\uploadBT747.bat dist\BT747_j2se.jar Latest/BT747_j2se.jar

bash -c "../myrsync.sh %PACK_DIR%/BT747_%DT%_full.zip /home/frs/project/bt747/Development"
REM twice to cope with certain communication errors.
%BASH% -c "../myrsync.sh %PACK_DIR%/BT747_%DT%_full.zip /home/frs/project/bt747/Development"

COPY %PACK_DIR%/BT747_%DT%_full.zip %PACK_DIR%/BT747_Latest_Full.zip
%BASH% -c "../myrsync.sh %PACK_DIR%/BT747_Latest_Full.zip /home/frs/project/bt747/Development"
REM twice to cope with certain communication errors.
%BASH% -c "../myrsync.sh %PACK_DIR%/BT747_Latest_Full.zip /home/frs/project/bt747/Development"

bash -c "../myrsync.sh %PACK_DIR%/bt747_osx_%DT%.tgz /home/frs/project/bt747/Development"
REM twice to cope with certain communication errors.
%BASH% -c "../myrsync.sh %PACK_DIR%/bt747_osx_%DT%.tgz /home/frs/project/bt747/Development"

cd src_j2se/bt747/j2se_view
%BASH% -c "./all_missing.sh"
ENDLOCAL
