del BT747*.zip
SET DT=20070716_002153

SET EXTRA_FILES=run_ex.bat webstart/Waba_only.jar win32comm.jar

zip -r BT747_%DT%.zip  %EXTRA_FILES% ChangeLog.txt src dist license.txt README.txt -xi src/CVS/\* \*/CVS/\*
curl --ftp-pasv --ftp-skip-pasv-ip -u anonymous:m.deweerd@ieee.org -T BT747_%DT%.zip ftp://upload.sourceforge.net/incoming/BT747_%DT%.zip
