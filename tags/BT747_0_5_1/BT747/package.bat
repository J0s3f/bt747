del BT747*.zip
SET DT=25_06_2007
SET VERSION=24_06_2007

zip -r BT747_%VERSION%.zip src dist license.txt README.txt -xi src/CVS/\* \*/CVS/\*
rem curl -v -u anonymous:m.deweerd@ieee.org --upload-file BT747_%DT%.zip ftp://upload.sourceforge.net/incoming
curl --ftp-pasv --ftp-skip-pasv-ip -u anonymous:m.deweerd@ieee.org -T BT747_%DT%.zip ftp://upload.sourceforge.net/incoming/BT747_%DT%.zip
