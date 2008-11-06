#./BT747cmd.bat -f c:/BT747/TST -b c:/BT747/in.csv --outtype CSV,NMEA
#./BT747cmd.bat -f n/tst -b c:/BT747/in.csv --outtype CSV,NMEA
CALL ./BT747cmd.bat -f n/tst -b c:/BT747/in.csv --outtype CSV,NMEA
CALL ./BT747cmd.bat -f n/tst2 -b n/tst.nmea --outtype CSV,NMEA
