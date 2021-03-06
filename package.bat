SETLOCAL
SET DT=2.2.1
SET APP_LANG=en
SET PACK_DIR=pack
SET PATH=%PATH%;C:\\CYGWIN\BIN;C:\\CYGWIN64\BIN
dos2unix *.sh
dos2unix *.command

SET FILES=

if %APP_LANG%==zh SET FILES=%FILES% UFFChi_H.pdb UFFChi_L.pdb
if %APP_LANG%==zh SET APP_LANG=zh_beta

if %APP_LANG%==jp SET FILES=%FILES% UFFJap_H.pdb UFFJap_L.pdb
if %APP_LANG%==jp SET APP_LANG=jp_beta

if %APP_LANG%==ko SET FILES=%FILES% UFFKor_H.pdb UFFKor_L.pdb
if %APP_LANG%==ko SET APP_LANG=ko_beta

SET FILES=%FILES% ChangeLog.txt src* dist COPYING  README.txt  build.xml .project .classpath default.properties nbproject 
SET FILES=%FILES% 5SW.pdb
SET FILES=%FILES% run_ex.bat run_rxtx.bat run_rxtx.sh run_both.bat run_j2se.bat
SET FILES=%FILES% bt747_macosX.command BT747_l.jnlp BT747_lwin.jnlp
SET FILES=%FILES% lib/Waba_only.jar lib/win32comm.jar lib/win32/javax.comm.properties lib/win32com.dll  lib/comm.jar lib/RXTXcomm.jar lib/swing-layout-1.0.3.jar
SET RXTXFILES=%FILES% lib/rxtx-2.1-7-bins-r2
SET EXCLUDEFILES=nbproject/private/\* \*/src/gps/parser/\*

del %PACK_DIR%\BT747_%DT%_%APP_LANG%_*.zip
mkdir %PACK_DIR%
REM No more uploading RXTX - only a few download that file.
REM zip -9 -r BT747_%DT%_%APP_LANG%_norxtx.zip %FILES% -xi src/CVS/\* \*/.svn/\* \*/CVS/\* %EXCLUDEFILES%
zip -9 -r %PACK_DIR%/BT747_%DT%_%APP_LANG%_full.zip %FILES% %RXTXFILES% -xi \*/.svn/\* src/CVS/\* \*/CVS/\* nbproject/private %EXCLUDEFILES%
REM curl --ftp-pasv --ftp-skip-pasv-ip -u anonymous:EMAILACCOUNT -T %PACK_DIR%/BT747_%DT%_%APP_LANG%_full.zip ftp://upload.sourceforge.net/incoming/BT747_%DT%_%APP_LANG%_full.zip
ECHO rsync -y -v --rsh="ssh -l mdeweerd" %PACK_DIR%/BT747_%DT%_%APP_LANG%_full.zip web.sourceforge.net:files/Development/BT747_%DT%_%APP_LANG%_full.zip
rsync --progress -y -v --rsh="ssh -l mdeweerd" %PACK_DIR%/BT747_%DT%_%APP_LANG%_full.zip web.sourceforge.net:files/Development/BT747_%DT%_%APP_LANG%_full.zip
REM curl --ftp-pasv --ftp-skip-pasv-ip -u anonymous:EMAILACCOUNT -T BT747_%DT%_%APP_LANG%_norxtx.zip ftp://upload.sourceforge.net/incoming/BT747_%DT%_%APP_LANG%_norxtx.zip
ENDLOCAL
