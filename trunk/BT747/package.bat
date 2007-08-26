del BT747*.zip
SET DT=20070827_005925

SET EXTRA_FILES=run_ex.bat run_linux.sh webstart/Waba_only.jar win32comm.jar win32/javax.comm.properties win32com.dll Linux/BT747.jar Linux/javax.comm.properties Linux/swserial.properties Linux/librxtxSerial.so Linux/librxtxParallel.so  webstart/comm.jar webstart/RXTXcomm.jar build.xml .project .classpath

zip -r BT747_%DT%.zip  %EXTRA_FILES% build/BT747.jar ChangeLog.txt src dist license.txt README.txt -xi src/CVS/\* \*/CVS/\*
curl --ftp-pasv --ftp-skip-pasv-ip -u anonymous:m.deweerd@ieee.org -T BT747_%DT%.zip ftp://upload.sourceforge.net/incoming/BT747_%DT%.zip
