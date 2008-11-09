#./BT747cmd.bat -f c:/BT747/TST -b c:/BT747/in.csv --outtype CSV,NMEA
#./BT747cmd.bat -f n/tst -b c:/BT747/in.csv --outtype CSV,NMEA
CALL ./BT747cmd.bat -f n/tst -b c:/BT747/in.csv --outtype CSV,NMEA
CALL ./BT747cmd.bat --splittype DAY -f n/tst2 -b n/tst.nmea --outtype CSV,NMEA
CALL ./BT747cmd.bat --splittype TRACK --timesplit 1 -f n/tst2 -b n/tst.nmea --outtype CSV,NMEA
CALL ./BT747cmd.bat --splittype TRACK --timesplit 1 -f n/tst3 -b c:/BT747/in.csv --outtype CSV,NMEA --height
CALL ./BT747cmd.bat --splittype TRACK --timesplit 1 -f n/tst4 -b c:/BT747/in.csv --outtype CSV,NMEA
diff n/tst2-*1036.csv n/tst3-*1036.csv
