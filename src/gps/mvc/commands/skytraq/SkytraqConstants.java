/**
 * 
 */
package gps.mvc.commands.skytraq;

/**
 * @author Mario De Weerd
 * 
 */
public class SkytraqConstants {
    /* Input System Messages */
    /* ID(Hex) ID(Decimal) Attribute Name Descriptions */
    /** 0x1 1 Input System Restart Force system to restart. */
    public final static int SKYTRAQ_RESTART = 0x01;
    /**
     * 0x2 2 Input Query Software version Query revision information of
     * software.
     */
    public final static int SKYTRAQ_Q_SW_REVISION = 0x02;
    /** 0x3 3 Input Query Software CRC Query the CRC of the software. */
    public final static int SKYTRAQ_Q_SW_CRC = 0x03;
    /**
     * 0x4 4 Input Set Factory Defaults Set system to factory default values.<br>
     * <b>Message Body</b>:<br>
     * byte 0: 0x00 = Reserved, 0x01 = Reboot.
     * */
    public final static int SKYTRAQ_SET_FACTORY_DEFAULTS = 0x04;
    /**
     * 0x5 5 Input Configure Serial Port Set up serial port COM, baud rate,
     * data bits, stop bits and parity.<br>
     * <b>Message Body (3 bytes) </b>:<br>
     * byte 0: COM Port, 00 = Port 1<br>
     * byte 1:
     * <table>
     * <tr>
     * <td>0</td>
     * <td>4800</td>
     * </tr>
     * <tr>
     * <td>1</td>
     * <td>9600</td>
     * </tr>
     * <tr>
     * <td>2</td>
     * <td>19200</td>
     * </tr>
     * <tr>
     * <td>3</td>
     * <td>38400</td>
     * </tr>
     * <tr>
     * <td>4</td>
     * <td>57600</td>
     * </tr>
     * <tr>
     * <td>5</td>
     * <td>115200</td>
     * </tr>
     * </table>
     * byte 2: 0x00 = Update to SRAM, 0x01 = Update to SRAM and FLASH.
     */
    public final static int SKYTRAQ_CONF_SERIALPORT = 0x05;
    /** 0x6 6 Input Reserved Reserved. */
    public final static int SKYTRAQ_RESERVED_6 = 0x06;
    /** 0x7 7 Input Reserved Reserved. */
    public final static int SKYTRAQ_RESERVED_7 = 0x07;
    /**
     * 0x8 8 Input Configure NMEA Configure NMEA output message.<br>
     * Set interval period in seconds. Message Body = 8 bytes.<br>
     * 7 bytes = GGA, GSA, GSV, GLL, RMC, VTG, ZDA intervals<br>
     * 1 byte = 0x00 = Update to SRAM, 0x01 = Update to SRAM and FLASH.
     */
    public final static int SKYTRAQ_SET_NMEAOUTPUT = 0x08;
    /**
     * 0x9 9 Input Configure Output Message Format Configure the output
     * message format from GPS receiver.<br>
     * Message Body (1 byte):<br>
     * Byte 0:
     * <ul>
     * <li>0x00 = No output.</li>
     * <li>0x01 = NMEA message.</li>
     * <li>0x02 = Binary message.</li>
     * </ul>
     */
    public final static int SKYTRAQ_SET_OUTPUT_MSG_FORMAT = 0x09;
    /**
     * 0xC 12 Input Configure Power Mode Set system power mode.<br>
     * Message Body (2 bytes):<br>
     * Byte 0:
     * <ul>
     * <li>0x00 = Normal.</li>
     * <li>0x01 = Power save.</li>
     * </ul>
     * <br>
     * Byte1:
     * <ul>
     * <li>0x00 = Update to SRAM.</li>
     * <li>0x01 = Update to SRAM and Flash.</li>
     * <li>0x02 = Enable temporary.</li>
     * </ul>
     */
    public final static int SKYTRAQ_CONF_POWER_MODE = 0x0C;
    /**
     * 0xE 14 Input Configure position update rate Configure the position
     * update rate of GPS system.<br>
     * * Message Body (2 bytes):<br>
     * Byte 0: Rate
     * <ul>
     * <li>1,2,4,5,8 or 10. When value is 4 or more, baud rate must be 38400
     * or more.</li>
     * </ul>
     * <br>
     * Byte1: Attributes
     * <ul>
     * <li>0x00 = Update to SRAM.</li>
     * <li>0x01 = Update to SRAM and Flash.</li>
     * </ul>
     */
    public final static int SKYTRAQ_CONF_UPDATE_RATE = 0x0E;
    /**
     * 0x10 16 Input Query position update rate Query the position update rate
     * of GPS system.<br>
     * <li>1,2,4,5,8 or 10.</li>
     */
    public final static int SKYTRAQ_Q_UPDATE_RATE = 0x10;

    // Every second, PVT solution is generated by the GPS kernel. Logging to
    // the serial flash memory is done according to
    // the following rule:
    // Tdiff = time of current fix � time of last stored position fix
    // Diff_Dx = absolute distance between current fix and last stored
    // position fix in ECEF X axis
    // Diff_Dy = absolute distance between current fix and last stored
    // position fix in ECEF Y axis
    // Diff_Dz = absolute distance between current fix and last stored
    // position fix in ECEF Z axis
    // V = speed of current Fix
    // Tth_max = threshold of maximum time
    // Tth_mim = threshold of minimum time
    // Dth_max = threshold of maximum distance
    // Dth_mim = threshold of minimum distance
    // Vth_max = threshold of maximum speed
    // Vth_mim = threshold of minimum speed
    // Ddiff = sqrt(Diff_Dx * Diff_Dx + Diff_Dy * Diff_Dy + Diff_Dz * Diff_Dz)
    // Tdiff-min = Tdiff - Tth_mim
    // Ddiff-min = Ddiff - Dth_mim
    // Tdiff-max = Tdiff - Tth_max
    // Ddiff-max = Ddiff - Dth_max
    // if (((Tdiff-min > 0) && (Ddiff-min >= 0) && (V >= Vth_mim)) ||
    // (Tdiff-max > 0) || (Ddiff-max > 0) || (V > Vth_max))
    // {
    // if ((Boundary of FLASH Sector) || (Diff_Dx > 511) | (Diff_Dy > 511) |
    // (Diff_Dz > 511) || (Tdiff > 65535))
    // Store a full data entry to the flash.
    // else
    // Store a compact data entry to the flash.
    // }
    
    /** Log download procedure: */
    // - PC sends a LOG STATUS binary message to retrieve information on how
    // many sectors of data being
    // logged by GPS receiver.
    // - PC then change the baud rate of receiver to 115200 if it�s not a
    // Bluetooth GPS receiver.
    // - PC will then send LOG READ BATCH binary message to request GPS
    // receiver to start output log data from
    // the starting log sector to the ending log sector. Following the output
    // log data, GPS receive will send END
    // and CHECKSUM messages to PC.
    // - PC then verifies the received log data
    // against the received checksum as
    // an indication of a failure or success. Once it is a success, PC will
    // continue the next starting sector read to
    // next ending sector until all the log data being read. On the other
    // hand, if it is a failure, the same sectors will
    // be retried again until success.
    /**
     * LOG STATUS CONTROL � Request Information of the Log Buffer Status
     * (0x17).
     */
    public final static int SKYTRAQ_LOG_STATUS_CONTROL = 0x17;
    /**
     * LOG CONFIGURE CONTROL � Configuration Data Logging Criteria (0x18).<br>
     * Message body (26 bytes): Byte 0-3: max_time<br>
     * Byte 4-7: min_time<br>
     * Byte 8-11: max_distance<br>
     * Byte 12-15: min_distance<br>
     * Byte 16-19: max_speed<br>
     * Byte 20-23: min_speed<br>
     * Byte 24: datalog_enable<br>
     * Byte 25: reserved<br>
     */
    public final static int SKYTRAQ_LOG_CONFIGURE_CONTROL = 0x18;
    /** LOG CLEAR CONTROL � Clear Data Logging Buffer (0x19). */
    public final static int SKYTRAQ_LOG_CLEAR_CONTROL = 0x19;
    /**
     * LOG READ BATCH CONTROL � Enable data read from the log buffer (0x1D).<br>
     * Message body (4 bytes):<br>
     * Byte 0-1: Starting log sector.<br>
     * Byte 2-3: End log sector.<br>
     */
    public final static int SKYTRAQ_LOG_READ_BATCH_CONTROL = 0x1D;
    /**
     * LOG STATUS OUTPUT � Output Status of the Log Buffer (0x94).<br>
     * Message body (34 bytes):<br>
     * Byte 0-3:  current log buffer address<br>
     * Byte 4-5:  total sectors<br>
     * Byte 6-7:  sectors left<br>
     * Byte 8-11: max_time<br>
     * Byte 12-15:min_time<br>
     * Byte 16-19:max_distance<br>
     * Byte 20-23:min_distance<br>
     * Byte 24-27:max_speed<br>
     * Byte 28-31:min_speed<br>
     * Byte 32: data log enable (0 = disable, 1 = enable).<br>
     * Byte 33: Log fifo mode (stop/overwrite)<br>
     * */
    public final static int SKYTRAQ_LOG_STATUS_OUTPUT = 0x94;

    /* Input GPS Messages. */
    /* ID(Hex) ID(Decimal) Attribute Name Descriptions. */
    /** 0x29 41 Input Configure Datum Configure Datum of the GPS receiver. */
    public final static int SKYTRAQ_CONF_DATUM = 0x29;
    /**
     * 0x2D 45 Input Query Datum Query datum used by the GPS receiver.<br>
     * Message Body: 2-3 index 0013 Refer to Appendix B for available Datum
     * UINT16<br>
     * 4 Ellip idx 07 Refer to Appendix A for available Value UINT8<br>
     * 5-6 Delta X FF7A<br>
     * Refer to Appendix A and B for available Delta X SINT16 Meter<br>
     * 7-8 Delta Y FF97 Refer to Appendix A and B for available Delta Y SINT16
     * Meter<br>
     * 9-10 Delta Z FED9 Refer to Appendix A and B for available Delta Z
     * SINT16 Meter<br>
     * 11-14 Semi-major axis 007DDF39 Refer to Appendix A UINT32<br>
     * 15-18 Inversed Flattening 0046F410 Refer to Appendix A UINT32<br>
     * 19 Attributes 00<br>
     * <ul>
     * <li>0x00 = Update to SRAM.</li>
     * <li>0x01 = Update to SRAM and Flash.</li>
     * </ul>
     */
    public final static int SKYTRAQ_Q_DATUM = 0x2D;
    /**
     * 0x30 48 Input Get ephemeris Retrieve ephemeris data of the GPS
     * receiver.<br>
     * Byte 0 = 0x00 = all SVs, other value = particular SV.
     */
    public final static int SKYTRAQ_GET_EMPHERIS = 0x30;
    /**
     * 0x31 49 Input Set ephemeris Set ephemeris data to the GPS receiver.<br>
     * bytes 0&1 = Sat ID. next 84 bytes = empheris data.
     */
    public final static int SKYTRAQ_SET_EMPHERIS = 0x31;
    /**
     * 0x37 55 Input Configure WAAS Configure the enable or disable of WAAS.<br>
     * Byte 0 = 0x00 = disable, 0x01 = enable.<br>
     * Byte 1:
     * <ul>
     * <li>0x00 = Update to SRAM.</li>
     * <li>0x01 = Update to SRAM and Flash.</li>
     * </ul>
     * */
    public final static int SKYTRAQ_CONF_WAAS = 0x37;
    /**
     * 0x38 56 Input Query WAAS status Query WAAS status of GPS receiver.<br>
     * Byte 0 = 0x00 = disable, 0x01 = enable.<br>
     */
    public final static int SKYTRAQ_Q_WAAS = 0x38;
    /**
     * 0x39 57 Input Configure position pinning Enable or disable position
     * pinning of GPS receiver.<br>
     * Message body = 1 byte<br>
     * Byte 0 = 0x00 = disable, 0x01 = enable.<br>
     */
    public final static int SKYTRAQ_CONF_POSITION_PINNING = 0x39;
    /**
     * 0x3A 58 Input Query position pinning Query position pinning status of
     * the GPS receiver. Message body = 0 byte<br>
     */
    public final static int SKYTRAQ_Q_POSITION_PINNING = 0x3A;
    /**
     * 0x3B 59 Input Configure position pinning parameters Set position
     * pinning parameters of GPS receiver.
     */
    public final static int SKYTRAQ_CONF_POSITION_PINNING_PARAMS = 0x3B;
    /**
     * 0x3C 60 Input Configuration navigation mode Configure the navigation
     * mode of GPS system.<br>
     * Message body = 10 byte<br>
     * Byte 0-1 = Pinning speed in km/h<br>
     * Byte 2-3 = Pinning count in second<br>
     * Byte 4-5 = unpinning speed in km/h<br>
     * Byte 6-7 = unpinning count in seconds<br>
     * Byte 8-9 = unpinning speed in meters<br>
     */
    public final static int SKYTRAQ_CONF_NAV_MODE = 0x3C;
    /**
     * 0x3D 61 Input Query navigation mode Query the navigation mode of GPS
     * receiver.<br>
     * Message body = 2 byte<br>
     * Byte 0 = 0x00 = car, 0x01 = pedestrian.<br>
     * Byte 1:
     * <ul>
     * <li>0x00 = Update to SRAM.</li>
     * <li>0x01 = Update to SRAM and Flash.</li>
     * </ul>
     */
    public final static int SKYTRAQ_Q_NAV_MODE = 0x3D;
    /**
     * 0x3E 62 Input Configure 1PPS mode Set 1PPS mode to the GPS receiver.<br>
     * Message body = 2 byte<br>
     * Byte 0 = 0x00 = off, 0x01 = on when 3D, 0x02 = on when 1 sat in view.<br>
     * Byte 1:
     * <ul>
     * <li>0x00 = Update to SRAM.</li>
     * <li>0x01 = Update to SRAM and Flash.</li>
     * </ul>
     */
    public final static int SKYTRAQ_CONF_1PPS_MODE = 0x3E;
    /** 0x3F 63 Input Query 1PPS mode Query 1PPS mode of the GPS receiver. */
    public final static int SKYTRAQ_Q_1PPS_MODE = 0x3F;

    /* Output System Messages */
    /* ID(Hex) ID(Decimal) Attribute Name Descriptions. */
    /**
     * 0x80 128 Output Software version Software revision of the receiver.<br>
     * Message body = 13 byte<br>
     * Byte 0 = Software Type, 0x00 = reserved, 0x01 = system code.<br>
     * Byte 1-5 = Kernel version [4 bytes, XXXX.YY.ZZ]<br>
     * Byte 5-9 = ODM version [4 bytes, XXXX.YY.ZZ]<br>
     * Byte 9-13 = Revision [4 bytes, XXXX.YY.ZZ]<br>
     */
    public final static int SKYTRAQ_D_SW_VERSION = 0x80;
    /**
     * 0x81 129 Output Software CRC Software CRC of the receiver.<br>
     * Message body = 3 byte<br>
     * Byte 0 = Software Type, 0x00 = reserved, 0x01 = system code.<br>
     * Byte 1-2 = CRC<br>
     */
    public final static int SKYTRAQ_D_SW_CRC = 0x81;
    /** 0x82 130 Output Reserved Reserved. */
    public final static int SKYTRAQ_D_RESERVED_1 = 0x82;
    /**
     * 0x83 131 Output ACK ACK to a successful input message.<br>
     * Message body = ID of corresponding message.
     */
    public final static int SKYTRAQ_ACK = 0x83;
    /**
     * 0x84 132 Output NACK Response to an unsuccessful input message.<br>
     * Message body = ID of corresponding message.
     */
    public final static int SKYTRAQ_NACK = 0x84;
    /**
     * 0x86 134 Output Position update rate Position update rate of GPS system
     * Output GPS Messages.<br>
     * Message body = 1 byte with the update rate (Hz).
     */
    public final static int SKYTRAQ_D_POS_UPDATE_RATE = 0x86;
    /**
     * 0xAE 174 Output GPS Datum Datum used by the GPS receiver.<br>
     * Message body (2 bytes):<br>
     * Byte 0-1: Datum index.
     */
    public final static int SKYTRAQ_D_DATUM = 0xAE;

    /**
     * 0xB3 179 Output GPS Ephemeris data � ephemeris data of the GPS receiver
     * (0xB1).
     */
    public final static int SKYTRAQ_D_EMPHERIS_DATA = 0xB1;

    /**
     * 0xB3 179 Output GPS WAAS status WAAS status of the GPS receiver.<br>
     * Message body (1 bytes):<br>
     * Byte 0: WAAS status, 0 = disable, 1 = enable.
     */
    public final static int SKYTRAQ_D_WAAS_STATUS = 0xB3;
    /**
     * 0xB4 180 Output GPS Position pinning status Position pinning status of
     * the GPS receiver.<br>
     * Message body (1 bytes):<br>
     * Byte 0: pinning status, 0 = disable, 1 = enable.
     */
    public final static int SKYTRAQ_D_POSITION_PINNING_STATUS = 0xB4;
    /**
     * 0xB5 181 Output GPS navigation mode Navigation mode of the GPS
     * receiver.<br>
     * Message body (1 byte): 0 = car, 1 = pedestrian.
     */
    public final static int SKYTRAQ_D_NAV_MODE = 0xB5;
    /**
     * 0xB6 182 Output GPS 1PPS mode 1PPS mode of GPS receiver.<br>
     * Message body (1 bytes):<br>
     * Byte 0: status, 0 = disable, 1 = enable.
     * 
     * */
    public final static int SKYTRAQ_D_1PPS_MODE = 0xB6;

    /** Appendix */
    // A. Ellipsoid List
    // Ellipsoid
    // Index Ellipsoid Semi-major axis
    // (a)
    // Inversed Flattening
    // (1/f)
    // 1 Airy 1830 6377563.396 299.3249646
    // 2 Modified Airy 6377340.189 299.3249646
    // 3 Australian National 6378160 298.25
    // 4 Bessel 1841 (Namibia) 6377483.865 299.1528128
    // 5 Bessel 1841 6377397.155 299.1528128
    // 6 Clarke 1866 6378206.4 294.9786982
    // 7 Clarke 1880 6378249.145 293.465
    // 8 Everest (India 1830) 6377276.345 300.8017
    // 9 Everest (Sabah Sarawak) 6377298.556 300.8017
    // 10 Everest (India 1956) 6377301.243 300.8017
    // 11 Everest (Malaysia 1969) 6377295.664 300.8017
    // 12 Everest (Malay. & Sing) 6377304.063 300.8017
    // 13 Everest (Pakistan) 6377309.613 300.8017
    // 14 Modified Fischer 1960 6378155 298.3
    // 15 Helmert 1906 6378200 298.3
    // 16 Hough 1960 6378270 297
    // 17 Indonesian 1974 6378160 298.247
    // 18 International 1924 6378388 297
    // 19 Krassovsky 1940 6378245 298.3
    // 20 GRS 80 6378137 298.257222101
    // 21 South American 1969 6378160 298.25
    // 22 WGS 72 6378135 298.26
    // 23 WGS 84 6378137 298.257223563

    // Datum
    // index Datum Name Delta
    // X
    // Delta
    // Y
    // Delta
    // Z Ellipsoid Ellipsoid
    // Index Region of Use
    // 0 WGS-84 0 0 0 WGS 84 23 Global
    // 1 Adindan -118 -14 218 Clarke 1880 7 Burkina Faso
    // 2 Adindan -134 -2 210 Clarke 1880 7 Cameroon
    // 3 Adindan -165 -11 206 Clarke 1880 7 Ethiopia
    // 4 Adindan -123 -20 220 Clarke 1880 7 Mali
    // 5 Adindan -166 -15 204 Clarke 1880 7 MEAN FOR Ethiopia;
    // Sudan
    // 6 Adindan -128 -18 224 Clarke 1880 7 Senegal
    // 7 Adindan -161 -14 205 Clarke 1880 7 Sudan
    // 8 Afgooye -43 -163 45 Krassovsky 1940 19 Somalia
    // 9 Ain el Abd 1970 -150 -250 -1 International 1924 18 Bahrain
    // 10 Ain el Abd 1970 -143 -236 7 International 1924 18 Saudi Arabia
    // 11 American Samoa 1962 -115 118 426 Clarke 1866 6 American Samoa
    // Islands
    // 12 Anna 1 Astro 1965 -491 -22 435 Australian National 3 Cocos Islands
    // 13 Antigua Island Astro 1943 -270 13 62 Clarke 1880 7 Antigua (Leeward
    // Islands)
    // 14 Arc 1950 -138 -105 -289 Clarke 1880 7 Botswana
    // 15 Arc 1950 -153 -5 -292 Clarke 1880 7 Burundi
    // 16 Arc 1950 -125 -108 -295 Clarke 1880 7 Lesotho
    // 17 Arc 1950 -161 -73 -317 Clarke 1880 7 Malawi
    // 18 Arc 1950 -143 -90 -294 Clarke 1880 7
    // MEAN FOR Botswana;
    // Lesotho; Malawi;
    // Swaziland; Zaire; Zambia;
    // Zimbabwe
    // 19 Arc 1950 -134 -105 -295 Clarke 1880 7 Swaziland
    // 20 Arc 1950 -169 -19 -278 Clarke 1880 7 Zaire
    // 21 Arc 1950 -147 -74 -283 Clarke 1880 7 Zambia
    // 22 Arc 1950 -142 -96 -293 Clarke 1880 7 Zimbabwe
    // 23 Arc 1960 -160 -6 -302 Clarke 1880 7 MEAN FOR Kenya;
    // Tanzania
    // 24 Arc 1960 -157 -2 -299 Clarke 1880 7 Kenya
    // 25 Arc 1960 -175 -23 -303 Clarke 1880 7 Taanzania
    // 26 Ascension Island 1958 -205 107 53 International 1924 18 Ascension
    // Island
    // 27 Astro Beacon E 1945 145 75 -272 International 1924 18 Iwo Jima
    // 28 Astro DOS 71/4 -320 550 -494 International 1924 18 St Helena Island
    // 29 Astro Tern Island (FRIG)
    // 1961
    // 114 -116 -333 International 1924 18 Tern Island
    // 30 Astronomical Station 1952 124 -234 -25 International 1924 18 Marcus
    // Island
    // 31 Australian Geodetic 1966 -133 -48 148 Australian National 3
    // Australia; Tasmania
    // 32 Australian Geodetic 1984 -134 -48 149 Australian National 3
    // Australia; Tasmania
    // 33 Ayabelle Lighthouse -79 -129 145 Clarke 1880 7 Djibouti
    // 34 Bellevue (IGN) -127 -769 472 International 1924 18 Efate & Erromango
    // Islands
    // 35 Bermuda 1957 -73 213 296 Clarke 1866 6 Bermuda
    // 36 Bissau -173 253 27 International 1924 18 Guinea-Bissau
    // 37 Bogota Observatory 307 304 -318 International 1924 18 Colombia
    // 38 Bukit Rimpah -384 664 -48 Bessel 1841 5 Indonesia (Bangka &
    // Belitung Ids)
    // 39 Camp Area Astro -104 -129 239 International 1924 18 Antarctica
    // (McMurdo
    // Camp Area)
    // 40 Campo Inchauspe -148 136 90 International 1924 18 Argentina
    // 41 Canton Astro 1966 298 -304 -375 International 1924 18 Phoenix
    // Islands
    // 42 Cape -136 -108 -292 Clarke 1880 7 South Africa
    // 43 Cape Canaveral -2 151 181 Clarke 1866 6 Bahamas; Florida
    // 44 Carthage -263 6 431 Clarke 1880 7 Tunisia
    // 45 Chatham Island Astro
    // 1971 175 -38 113 International 1924 18 New Zealand (Chatham
    // Island)
    // 46 Chua Astro -134 229 -29 International 1924 18 Paraguay
    // 47 Corrego Alegre -206 172 -6 International 1924 18 Brazil
    // 48 Dabola -83 37 124 Clarke 1880 7 Guinea
    // 49 Deception Island 260 12 -147 Clarke 1880 7 Deception Island;
    // Antarctia
    // 50 Djakarta (Batavia) -377 681 -50 Bessel 1841 5 Indonesia (Sumatra)
    // 51 DOS 1968 230 -199 -752 International 1924 18 New Georgia Islands
    // (Gizo Island)
    // 52 Easter Island 1967 211 147 111 International 1924 18 Easter Island
    // 53 Estonia; Coordinate
    // System 1937 374 150 588 Bessel 1841 5 Estonia
    // 54 European 1950 -104 -101 -140 International 1924 18 Cyprus
    // 55 European 1950 -130 -117 -151 International 1924 18 Egypt
    // 56 European 1950 -86 -96 -120 International 1924 18
    // England; Channel Islands;
    // Scotland; Shetland
    // Islands
    // 57 European 1950 -86 -96 -120 International 1924 18
    // England; Ireland;
    // Scotland; Shetland
    // Islands
    // 58 European 1950 -87 -95 -120 International 1924 18 Finland; Norway
    // 59 European 1950 -84 -95 -130 International 1924 18 Greece
    // 60 European 1950 -117 -132 -164 International 1924 18 Iran
    // 61 European 1950 -97 -103 -120 International 1924 18 Italy (Sardinia)
    // 62 European 1950 -97 -88 -135 International 1924 18 Italy (Sicily)
    // 63 European 1950 -107 -88 -149 International 1924 18 Malta
    // 64 European 1950 -87 -98 -121 International 1924 18
    // MEAN FOR Austria;
    // Belgium; Denmark;
    // Finland; France; W
    // Germany; Gibraltar;
    // Greece; Italy;
    // Luxembourg;
    // Netherlands; Norway;
    // Portugal; Spain; Sweden;
    // Switzerland
    // 65 European 1950 -87 -96 -120 International 1924 18
    // MEAN FOR Austria;
    // Denmark; France; W
    // Germany; Netherlands;
    // Switzerland
    // 66 European 1950 -103 -106 -141 International 1924 18
    // MEAN FOR Iraq; Israel;
    // Jordan; Lebanon; Kuwait;
    // Saudi Arabia; Syria
    // 67 European 1950 -84 -107 -120 International 1924 18 Portugal; Spain
    // 68 European 1950 -112 -77 -145 International 1924 18 Tunisia
    // 69 European 1979 -86 -98 -119 International 1924 18
    // MEAN FOR Austria;
    // Finland; Netherlands;
    // Norway; Spain; Sweden;
    // Switzerland
    // 70 Fort Thomas 1955 -7 215 225 Clarke 1880 7
    // Nevis; St. Kitts (Leeward
    // Islands)
    // 71 Gan 1970 -133 -321 50 International 1924 18 Republic of Maldives
    // 72 Geodetic Datum 1949 84 -22 209 International 1924 18 New Zealand
    // 73 Graciosa Base SW 1948 -104 167 -38 International 1924 18
    // Azores (Faial; Graciosa;
    // Pico; Sao Jorge; Terceira)
    // 74 Guam 1963 -100 -248 259 Clarke 1866 6 Guam
    // 75 Gunung Segara -403 684 41 Bessel 1841 5 Indonesia (Kalimantan)
    // 76 GUX 1 Astro 252 -209 -751 International 1924 18 Guadalcanal Island
    // 77 Herat North -333 -222 114 International 1924 18 Afghanistan
    // 78 Hermannskogel Datum 653 -212 449 Bessel 1841
    // (Namibia) 4 Croatia -Serbia,
    // Bosnia-Herzegovina
    // 79 Hjorsey 1955 -73 46 -86 International 1924 18 Iceland
    // 80 Hong Kong 1963 -156 -271 -189 International 1924 18 Hong Kong
    // 81 Hu-Tzu-Shan -637 -549 -203 International 1924 18 Taiwan
    // 82 Indian 282 726 254 Everest (India 1830) 8 Bangladesh
    // 83 Indian 295 736 257 Everest (India 1956) 10 India; Nepal
    // 84 Indian 283 682 231 Everest (Pakistan) 13 Pakistan
    // 85 Indian 1954 217 823 299 Everest (India 1830) 8 Thailand
    // 86 Indian 1960 182 915 344 Everest (India 1830) 8 Vietnam (Con Son
    // Island)
    // 87 Indian 1960 198 881 317 Everest (India 1830) 8 Vietnam (Near 16�N))
    // 88 Indian 1975 210 814 289 Everest (India 1830) 8 Thailand
    // 89 Indonesian 1974 -24 -15 5 Indonesian 1974 17 Indonesia
    // 90 Ireland 1965 506 -122 611 Modified Airy 2 Ireland
    // 91 ISTS 061 Astro 1968 -794 119 -298 International 1924 18 South
    // Georgia Islands
    // 92 ISTS 073 Astro 1969 208 -435 -229 International 1924 18 Diego Garcia
    // 93 Johnston Island 1961 189 -79 -202 International 1924 18 Johnston
    // Island
    // 94 Kandawala -97 787 86 Everest (India 1830) 8 Sri Lanka
    // 95 Kerguelen Island 1949 145 -187 103 International 1924 18 Kerguelen
    // Island
    // 96 Kertau 1948 -11 851 5
    // Everest (Malay. &
    // Sing) 12
    // West Malaysia &
    // Singapore
    // 97 Kusaie Astro 1951 647 1777 -1124 International 1924 18 Caroline
    // Islands
    // 98 Korean Geodetic System 0 0 0 GRS 80 20 South Korea
    // 99 L. C. 5 Astro 1961 42 124 147 Clarke 1866 6 Cayman Brac Island
    // 100 Leigon -130 29 364 Clarke 1880 7 Ghana
    // 101 Liberia 1964 -90 40 88 Clarke 1880 7 Liberia
    // 102 Luzon -133 -77 -51 Clarke 1866 6 Philippines (Excluding
    // Mindanao)
    // 103 Luzon -133 -79 -72 Clarke 1866 6 Philippines (Mindanao)
    // 104 M'Poraloko -74 -130 42 Clarke 1880 7 Gabon
    // 105 Mahe 1971 41 -220 -134 Clarke 1880 7 Mahe Island
    // 106 Massawa 639 405 60 Bessel 1841 5 Ethiopia (Eritrea)
    // 107 Merchich 31 146 47 Clarke 1880 7 Morocco
    // 108 Midway Astro 1961 912 -58 1227 International 1924 18 Midway Islands
    // 109 Minna -81 -84 115 Clarke 1880 7 Cameroon
    // 110 Minna -92 -93 122 Clarke 1880 7 Nigeria
    // 111 Montserrat Island Astro
    // 1958 174 359 365 Clarke 1880 7 Montserrat (Leeward
    // Islands)
    // 112 Nahrwan -247 -148 369 Clarke 1880 7 Oman (Masirah Island)
    // 113 Nahrwan -243 -192 477 Clarke 1880 7 Saudi Arabia
    // 114 Nahrwan -249 -156 381 Clarke 1880 7 United Arab Emirates
    // 115 Naparima BWI -10 375 165 International 1924 18 Trinidad & Tobago
    // 116 North American 1927 -5 135 172 Clarke 1866 6 Alaska (Excluding
    // Aleutian Ids)
    // 117 North American 1927 -2 152 149 Clarke 1866 6 Alaska (Aleutian Ids
    // East
    // of 180�W)
    // 118 North American 1927 2 204 105 Clarke 1866 6 Alaska (Aleutian Ids
    // West
    // of 180�W)
    // 119 North American 1927 -4 154 178 Clarke 1866 6
    // Bahamas (Except San
    // Salvador Id)
    // 120 North American 1927 1 140 165 Clarke 1866 6 Bahamas (San Salvador
    // Island)
    // 121 North American 1927 -7 162 188 Clarke 1866 6 Canada (Alberta;
    // British
    // Columbia)
    // 122 North American 1927 -9 157 184 Clarke 1866 6
    // Canada (Manitoba;
    // Ontario)
    // 123 North American 1927 -22 160 190 Clarke 1866 6
    // Canada (New Brunswick;
    // Newfoundland; Nova
    // Scotia; Quebec)
    // 124 North American 1927 4 159 188 Clarke 1866 6
    // Canada (Northwest
    // Territories;
    // Saskatchewan)
    // 125 North American 1927 -7 139 181 Clarke 1866 6 Canada (Yukon)
    // 126 North American 1927 0 125 201 Clarke 1866 6 Canal Zone
    // 127 North American 1927 -9 152 178 Clarke 1866 6 Cuba
    // 128 North American 1927 11 114 195 Clarke 1866 6
    // Greenland (Hayes
    // Peninsula)
    // 129 North American 1927 -3 142 183 Clarke 1866 6
    // MEAN FOR Antigua;
    // Barbados; Barbuda;
    // Caicos Islands; Cuba;
    // Dominican Republic;
    // Grand Cayman; Jamaica;
    // Turks Islands
    // 130 North American 1927 0 125 194 Clarke 1866 6
    // MEAN FOR Belize; Costa
    // Rica; El Salvador;
    // Guatemala; Honduras;
    // Nicaragua
    // 131 North American 1927 -10 158 187 Clarke 1866 6 MEAN FOR Canada
    // 132 North American 1927 -8 160 176 Clarke 1866 6 MEAN FOR CONUS
    // 133 North American 1927 -9 161 179 Clarke 1866 6
    // MEAN FOR CONUS
    // (East of Mississippi; River
    // Including Louisiana;
    // Missouri; Minnesota)
    // 134 North American 1927 -8 159 175 Clarke 1866 6
    // MEAN FOR CONUS
    // (West of Mississippi; River
    // Excluding Louisiana;
    // Minnesota; Missouri)
    // 135 North American 1927 -12 130 190 Clarke 1866 6 Mexico
    // 136 North American 1983 0 0 0 GRS 80 20
    // Alaska (Excluding
    // Aleutian Ids)
    // 137 North American 1983 -2 0 4 GRS 80 20 Aleutian Ids
    // 138 North American 1983 0 0 0 GRS 80 20 Canada
    // 139 North American 1983 0 0 0 GRS 80 20 CONUS
    // 140 North American 1983 1 1 -1 GRS 80 20 Hawaii
    // 141 North American 1983 0 0 0 GRS 80 20 Mexico; Central America
    // 142 North Sahara 1959 -186 -93 310 Clarke 1880 7 Algeria
    // 143 Observatorio
    // Meteorologico 1939 -425 -169 81 International 1924 18 Azores (Corvo &
    // Flores
    // Islands)
    // 144 Old Egyptian 1907 -130 110 -13 Helmert 1906 15 Egypt
    // 145 Old Hawaiian 89 -279 -183 Clarke 1866 6 Hawaii
    // 146 Old Hawaiian 45 -290 -172 Clarke 1866 6 Kauai
    // 147 Old Hawaiian 65 -290 -190 Clarke 1866 6 Maui
    // 148 Old Hawaiian 61 -285 -181 Clarke 1866 6 MEAN FOR Hawaii;
    // Kauai; Maui; Oahu
    // 149 Old Hawaiian 58 -283 -182 Clarke 1866 6 Oahu
    // 150 Oman -346 -1 224 Clarke 1880 7 Oman
    // 151 Ordnance Survey Great
    // Britain 1936 371 -112 434 Airy 1830 1 England
    // 152
    // Ordnance Survey Great
    // Britain 1936 371 -111 434 Airy 1830 1
    // England; Isle of Man;
    // Wales
    // 153 Ordnance Survey Great
    // Britain 1936 375 -111 431 Airy 1830 1
    // MEAN FOR England; Isle
    // of Man; Scotland;
    // Shetland Islands; Wales
    // 154 Ordnance Survey Great
    // Britain 1936 384 -111 425 Airy 1830 1 Scotland; Shetland
    // Islands
    // 155 Ordnance Survey Great
    // Britain 1936 370 -108 434 Airy 1830 1 Wales
    // 156 Pico de las Nieves -307 -92 127 International 1924 18 Canary
    // Islands
    // 157 Pitcairn Astro 1967 185 165 42 International 1924 18 Pitcairn
    // Island
    // 158 Point 58 -106 -129 165 Clarke 1880 7 MEAN FOR Burkina Faso
    // & Niger
    // 159 Pointe Noire 1948 -148 51 -291 Clarke 1880 7 Congo
    // 160 Porto Santo 1936 -499 -249 314 International 1924 18
    // Porto Santo; Madeira
    // Islands
    // 161 Provisional South
    // American 1956 -270 188 -388 International 1924 18 Bolivia
    // 162 Provisional South
    // American 1956 -270 183 -390 International 1924 18 Chile (Northern; Near
    // 19
    // �S)
    // 163 Provisional South
    // American 1956
    // -305 243 -442 International 1924 18
    // Chile (Southern; Near 43
    // �S)
    // 164 Provisional South
    // American 1956 -282 169 -371 International 1924 18 Colombia
    // 165 Provisional South
    // American 1956 -278 171 -367 International 1924 18 Ecuador
    // 166 Provisional South
    // American 1956
    // -298 159 -369 International 1924 18 Guyana
    // 167 Provisional South
    // American 1956 -288 175 -376 International 1924 18
    // MEAN FOR Bolivia; Chile;
    // Colombia; Ecuador;
    // Guyana; Peru; Venezuela
    // 168 Provisional South
    // American 1956 -279 175 -379 International 1924 18 Peru
    // 169 Provisional South
    // American 1956 -295 173 -371 International 1924 18 Venezuela
    // 170 Provisional South Chilean
    // 1963 16 196 93 International 1924 18 Chile (Near 53 �S) (Hito
    // XVIII)
    // 171 Puerto Rico 11 72 -101 Clarke 1866 6 Puerto Rico; Virgin Islands
    // 172 Pulkovo 1942 28 -130 -95 Krassovsky 1940 19 Russia
    // 173 Qatar National -128 -283 22 International 1924 18 Qatar
    // 174 Qornoq 164 138 -189 International 1924 18 Greenland (South)
    // 175 Reunion 94 -948 -1262 International 1924 18 Mascarene Islands
    // 176 Rome 1940 -225 -65 9 International 1924 18 Italy (Sardinia)
    // 177 S-42 (Pulkovo 1942) 28 -121 -77 Krassovsky 1940 19 Hungary
    // 178 S-42 (Pulkovo 1942) 23 -124 -82 Krassovsky 1940 19 Poland
    // 179 S-42 (Pulkovo 1942) 26 -121 -78 Krassovsky 1940 19 Czechoslavakia
    // 180 S-42 (Pulkovo 1942) 24 -124 -82 Krassovsky 1940 19 Latvia
    // 181 S-42 (Pulkovo 1942) 15 -130 -84 Krassovsky 1940 19 Kazakhstan
    // 182 S-42 (Pulkovo 1942) 24 -130 -92 Krassovsky 1940 19 Albania
    // 183 S-42 (Pulkovo 1942) 28 -121 -77 Krassovsky 1940 19 Romania
    // 184 S-JTSK 589 76 480 Bessel 1841 5 Czechoslavakia (Prior 1
    // JAN 1993)
    // 185 Santo (DOS) 1965 170 42 84 International 1924 18 Espirito Santo
    // Island
    // 186 Sao Braz -203 141 53 International 1924 18 Azores (Sao Miguel;
    // Santa Maria Ids)
    // 187 Sapper Hill 1943 -355 21 72 International 1924 18 East Falkland
    // Island
    // 188 Schwarzeck 616 97 -251 Bessel 1841
    // (Namibia) 4 Namibia
    // 189 Selvagem Grande 1938 -289 -124 60 International 1924 18 Salvage
    // Islands
    // 190 Sierra Leone 1960 -88 4 101 Clarke 1880 7 Sierra Leone
    // 191 South American 1969 -62 -1 -37 South American 1969 21 Argentina
    // 192 South American 1969, -61 2 -48 South American 1969 21 Bolivia
    // 193 South American 1969, -60 -2 -41 South American 1969 21 Brazil
    // 194 South American 1969, -75 -1 -44 South American 1969 21 Chile
    // 195 South American 1969, -44 6 -36 South American 1969 21 Colombia
    // 196 South American 1969, -48 3 -44 South American 1969 21 Ecuador
    // 197 South American 1969, -47 26 -42 South American 1969 21 Ecuador
    // (Baltra;
    // Galapagos)
    // 198 South American 1969, -53 3 -47 South American 1969 21 Guyana
    // 199 South American 1969, -57 1 -41 South American 1969 21
    // MEAN FOR Argentina;
    // Bolivia; Brazil; Chile;
    // Colombia; Ecuador;
    // Guyana; Paraguay; Peru;
    // Trinidad & Tobago;
    // Venezuela
    // 200 South American 1969, -61 2 -33 South American 1969 21 Paraguay
    // 201 South American 1969, -58 0 -44 South American 1969 21 Peru
    // 202 South American 1969, -45 12 -33 South American 1969 21 Trinidad &
    // Tobago
    // 203 South American 1969, -45 8 -33 South American 1969 21 Venezuela
    // 204 South Asia 7 -10 -26 Modified Fischer
    // 1960 14 Singapore
    // 205 Tananarive Observatory
    // 1925
    // -189 -242 -91 International 1924 18 Madagascar
    // 206 Timbalai 1948 -679 669 -48 Everest (Sabah
    // Sarawak) 9 Brunei; E. Malaysia
    // (Sabah Sarawak)
    // 207 Tokyo -148 507 685 Bessel 1841 5 Japan
    // 208 Tokyo -148 507 685 Bessel 1841 5 MEAN FOR Japan; South
    // Korea; Okinawa
    // 209 Tokyo -158 507 676 Bessel 1841 5 Okinawa
    // 210 Tokyo -147 506 687 Bessel 1841 5 South Korea
    // 211 Tristan Astro 1968 -632 438 -609 International 1924 18 Tristan da
    // Cunha
    // 212 Viti Levu 1916 51 391 -36 Clarke 1880 7 Fiji (Viti Levu Island)
    // 213 Voirol 1960 -123 -206 219 Clarke 1880 7 Algeria
    // 214 Wake Island Astro 1952 276 -57 149 International 1924 18 Wake Atoll
    // 215 Wake-Eniwetok 1960 102 52 -38 Hough 1960 16 Marshall Islands
    // 216 WGS 1972 0 0 0 WGS 72 22 Global Definition
    // 217 Yacare -155 171 37 International 1924 18 Uruguay
    // 218 Zanderij -265 120 -358 International 1924 18 Suriname
}
