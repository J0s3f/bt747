SETLOCAL
SET DT=1.41
SET APP_LANG=en
del BT747*_%LANG%_*.zip
dos2unix *.sh
dos2unix *.command

SET FILES=

if %APP_LANG%==zh SET FILES=%FILES% UFFChi_H.pdb UFFChi_L.pdb
if %APP_LANG%==zh SET APP_LANG=zh_beta

if %APP_LANG%==jp SET FILES=%FILES% UFFJap_H.pdb UFFJap_L.pdb
if %APP_LANG%==jp SET APP_LANG=jp_beta

if %APP_LANG%==ko SET FILES=%FILES% UFFKor_H.pdb UFFKor_L.pdb
if %APP_LANG%==ko SET APP_LANG=ko_beta

SET FILES=%FILES% ChangeLog.txt src* dist license.txt README.txt  build.xml .project .classpath default.properties nbproject 
SET FILES=%FILES% 5SW.pdb
SET FILES=%FILES% run_ex.bat run_rxtx.bat run_rxtx.sh
SET FILES=%FILES% bt747_macosX.command
SET FILES=%FILES% webstart/Waba_only.jar win32comm.jar win32/javax.comm.properties win32com.dll  webstart/comm.jar webstart/RXTXcomm.jar
SET RXTXFILES=%FILES% rxtx-2.1-7-bins-r2
SET EXCLUDEFILES=nbproject/private

del BT747_%DT%_%APP_LANG%_*.zip
REM No more uploading RXTX - only a few download that file.
REM zip -9 -r BT747_%DT%_%APP_LANG%_norxtx.zip %FILES% -xi src/CVS/\* \*/CVS/\* %EXCLUDEFILES%
zip -9 -r BT747_%DT%_%APP_LANG%_full.zip %FILES% %RXTXFILES% -xi src/CVS/\* \*/CVS/\* nbproject/private
curl --ftp-pasv --ftp-skip-pasv-ip -u anonymous:m.deweerd@ieee.org -T BT747_%DT%_%APP_LANG%_full.zip ftp://upload.sourceforge.net/incoming/BT747_%DT%_%APP_LANG%_full.zip
REM curl --ftp-pasv --ftp-skip-pasv-ip -u anonymous:m.deweerd@ieee.org -T BT747_%DT%_%APP_LANG%_norxtx.zip ftp://upload.sourceforge.net/incoming/BT747_%DT%_%APP_LANG%_norxtx.zip
ENDLOCAL
