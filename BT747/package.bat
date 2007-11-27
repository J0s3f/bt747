del BT747*.zip
dos2unix *.sh
dos2unix *.command
SET DT=1.16

SET FILES=ChangeLog.txt src dist license.txt README.txt  build.xml .project .classpath default.properties
SET FILES=%FILES% run_ex.bat run_rxtx.bat run_rxtx.sh
SET FILES=%FILES% bt747_macosX.command
SET FILES=%FILES% webstart/Waba_only.jar win32comm.jar win32/javax.comm.properties win32com.dll  webstart/comm.jar webstart/RXTXcomm.jar
SET RXTXFILES=%FILES% rxtx-2.1-7-bins-r2

del BT747_%DT%*.zip
REM No more uploading RXTX - only a few download that file.
REM zip -9 -r BT747_%DT%_norxtx.zip %FILES% -xi src/CVS/\* \*/CVS/\*
zip -9 -r BT747_%DT%_full.zip %FILES% %RXTXFILES% -xi src/CVS/\* \*/CVS/\*
curl --ftp-pasv --ftp-skip-pasv-ip -u anonymous:m.deweerd@ieee.org -T BT747_%DT%_full.zip ftp://upload.sourceforge.net/incoming/BT747_%DT%_full.zip
REM curl --ftp-pasv --ftp-skip-pasv-ip -u anonymous:m.deweerd@ieee.org -T BT747_%DT%_norxtx.zip ftp://upload.sourceforge.net/incoming/BT747_%DT%_norxtx.zip
