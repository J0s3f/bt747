del BT747*.zip
SET DT=20070818_220707
SET EXTRA_FILES=run_ex.bat run_linux.sh webstart/Waba_only.jar win32comm.jar win32/javax.comm.properties win32com.dll Linux/javax.comm.properties Linux/swserial.properties Linux/librxtxSerial.so Linux/librxtxParallel.so  webstart/comm.jar webstart/RXTXcomm.jar build_bt747.xml

zip -r BT747_%DT%.zip  %EXTRA_FILES% build/BT747.jar ChangeLog.txt src dist license.txt README.txt -xi src/CVS/\* \*/CVS/\*
curl --ftp-pasv --ftp-skip-pasv-ip -u anonymous:m.deweerd@ieee.org -T BT747_%DT%.zip ftp://upload.sourceforge.net/incoming/BT747_%DT%.zip
