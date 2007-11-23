#!/usr/bin/perl
#
# Copyright (C) 2007 Niccolo Rigacci
#
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# as published by the Free Software Foundation; either version 2
# of the License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
#
# Author:	Niccolo Rigacci <niccolo@rigacci.org>
#
# Version:	0.2	2007-11-20
#
# Control program for GPS units using the MediaTek (MTK) chipset.
# Tested to work with i-Blue 747 GPS data logger.
#

use strict;
# Use the getopts() function.
use Getopt::Std;
use File::Basename;
use vars qw($opt_b $opt_d $opt_E $opt_f $opt_h $opt_l $opt_m $opt_o $opt_p $opt_r $opt_t $opt_w $opt_x);
# Install the libdevice-serialport-perl Debian package.
use Device::SerialPort;
# Install the libtimedate-perl Debian package.
use Date::Format;

# Debug levels.
my $LOG_EMERG   = 0;
my $LOG_ALERT   = 1;
my $LOG_CRIT    = 2;
my $LOG_ERR     = 3;
my $LOG_WARNING = 4;
my $LOG_NOTICE  = 5;
my $LOG_INFO    = 6;
my $LOG_DEBUG   = 7;

my $NAME = basename($0);

# Size in bytes of data types.
my $SIZEOF_BYTE   = 1;
my $SIZEOF_WORD   = 2;
my $SIZEOF_LONG   = 4;
my $SIZEOF_FLOAT  = 4;
my $SIZEOF_DOUBLE = 8;

# Log format is stored as a bitmask field.
my $LOG_FORMAT_UTC         = 0x00001;
my $LOG_FORMAT_VALID       = 0x00002;
my $LOG_FORMAT_LATITUDE    = 0x00004;
my $LOG_FORMAT_LONGITUDE   = 0x00008;
my $LOG_FORMAT_HEIGHT      = 0x00010;
my $LOG_FORMAT_SPEED       = 0x00020;
my $LOG_FORMAT_HEADING     = 0x00040;
my $LOG_FORMAT_DSTA        = 0x00080;
my $LOG_FORMAT_DAGE        = 0x00100;
my $LOG_FORMAT_PDOP        = 0x00200;
my $LOG_FORMAT_HDOP        = 0x00400;
my $LOG_FORMAT_VDOP        = 0x00800;
my $LOG_FORMAT_NSAT        = 0x01000;
my $LOG_FORMAT_SID         = 0x02000;
my $LOG_FORMAT_ELEVATION   = 0x04000;
my $LOG_FORMAT_AZIMUTH     = 0x08000;
my $LOG_FORMAT_SNR         = 0x10000;
my $LOG_FORMAT_RCR         = 0x20000;
my $LOG_FORMAT_MILLISECOND = 0x40000;
my $LOG_FORMAT_DISTANCE    = 0x80000;

my $SIZEOF_LOG_UTC         = $SIZEOF_LONG;
my $SIZEOF_LOG_VALID       = $SIZEOF_WORD;
my $SIZEOF_LOG_LATITUDE    = $SIZEOF_DOUBLE;
my $SIZEOF_LOG_LONGITUDE   = $SIZEOF_DOUBLE;
my $SIZEOF_LOG_HEIGHT      = $SIZEOF_FLOAT;
my $SIZEOF_LOG_SPEED       = $SIZEOF_FLOAT;
my $SIZEOF_LOG_HEADING     = $SIZEOF_FLOAT;
my $SIZEOF_LOG_DSTA        = $SIZEOF_WORD;
my $SIZEOF_LOG_DAGE        = $SIZEOF_LONG;
my $SIZEOF_LOG_PDOP        = $SIZEOF_WORD;
my $SIZEOF_LOG_HDOP        = $SIZEOF_WORD;
my $SIZEOF_LOG_VDOP        = $SIZEOF_WORD;
my $SIZEOF_LOG_NSAT        = $SIZEOF_BYTE * 2;
my $SIZEOF_LOG_SID         = $SIZEOF_BYTE;
my $SIZEOF_LOG_SIDINUSE    = $SIZEOF_BYTE;
my $SIZEOF_LOG_NBRSATS     = $SIZEOF_WORD;
my $SIZEOF_LOG_ELEVATION   = $SIZEOF_WORD;
my $SIZEOF_LOG_AZIMUTH     = $SIZEOF_WORD;
my $SIZEOF_LOG_SNR         = $SIZEOF_WORD;
my $SIZEOF_LOG_RCR         = $SIZEOF_WORD;
my $SIZEOF_LOG_MILLISECOND = $SIZEOF_WORD;
my $SIZEOF_LOG_DISTANCE    = $SIZEOF_DOUBLE;

# A record separator has one of the following types.
my $SEP_TYPE_CHANGE_LOG_BITMASK    = 0x02;
my $SEP_TYPE_CHANGE_LOG_PERIOD     = 0x03;
my $SEP_TYPE_CHANGE_LOG_DISTANCE   = 0x04;
my $SEP_TYPE_CHANGE_LOG_SPEED      = 0x05;
my $SEP_TYPE_CHANGE_OVERWRITE_STOP = 0x06;
my $SEP_TYPE_CHANGE_START_STOP_LOG = 0x07;

# Values for the VALID field.
my $VALID_NO_FIX    = 0x0001;
my $VALID_SPS       = 0x0002;
my $VALID_DGPS      = 0x0004;
my $VALID_PPS       = 0x0008;
my $VALID_RTK       = 0x0010;
my $VALID_FRTK      = 0x0020;
my $VALID_ESTIMATED = 0x0040;
my $VALID_MANUAL    = 0x0080;
my $VALID_SIMULATOR = 0x0100;

# Values for the RCR field.
my $RCR_TIME     = 0x01;
my $RCR_SPEED    = 0x02;
my $RCR_DISTANCE = 0x04;
my $RCR_INTEREST = 0x08;

# Log data is retrieved in chunks of this size.
my $SIZEOF_CHUNK        = 0x800;
my $SIZEOF_BLOCK        = 0x10000;
my $SIZEOF_BLOCK_HEADER = 0x200;
my $SIZEOF_SEPARATOR    = 0x10;
   
my $GPX_EOL = "\n";

# Default timeout for packet wait (sec).
my $TIMEOUT = 5;
# Timeout for activity on device port (msec).
my $TIMEOUT_IDLE_PORT = 5000;

#-------------------------------------------------------------------------
# Global variablee.
#-------------------------------------------------------------------------
my $debug = $LOG_ERR;         # Default loggin level.
my $port  = '/dev/ttyUSB0';   # Default communication port.

# GPX global values.
my $gpx_trk_minlat =   90.0;
my $gpx_trk_minlon =  180.0;
my $gpx_trk_maxlat =  -90.0;
my $gpx_trk_maxlon = -180.0;
my $gpx_wpt_minlat =   90.0;
my $gpx_wpt_minlon =  180.0;
my $gpx_wpt_maxlat =  -90.0;
my $gpx_wpt_maxlon = -180.0;
my $gpx_trk_number = 0;
my $gpx_wpt_number = 0;

my $version;
my $release;
my $model_id;
my $ob;
my $ret;
my $log_format;
my $blocks;
my $memory_used;
my $number_of_records;
my $bytes_to_read;
my $fp;
my $fp_log;
my $offset;

# Record values.
my $record_utc;
my $record_valid;
my $record_latitude;
my $record_longitude;
my $record_height;
my $record_speed;
my $record_heading;
my $record_dsta;
my $record_dage;
my $record_pdop;
my $record_hdop;
my $record_vdop;
my $record_nsat_in_use;
my $record_nsat_in_view;
my $record_rcr;
my $record_millisecond;
my $record_distance;


#-------------------------------------------------------------------------
# Get options from command line.
#-------------------------------------------------------------------------
if (! getopts('b:d:EF:f:hl:m:o:p:r:tw') or $opt_h) {
    my $str1 = describe_log_format(0x00fff);
    my $str2 = describe_log_format(0xff000);
    print <<HELP;
Usage: $NAME [options]
Options:
    -p port                  Communication port, default: $port
    -f outfile               Base name for saved files (.bin, .gpx)
    -w                       Create a waypoints gpx file
    -t                       Create a tracks gpx file
    -E                       Erase data log memory
    -l {on|off}              Turn loggin ON/OFF
    -m {stop|overwrite}      Set STOP/OVERWRITE recording method on memory full
    -r time:distance:speed   Set logging triggers (zero to disable trigger):
                             every 1-999 seconds, every 10-9999 meters, over 10-999 km/h
    -o log_format            Enable or disable log fields (+FIELD1,-FIELD2,...), available fields:
                             $str1
                             $str2
    -b datalog.bin           Do not read device, read a previously saved binary datalog file
    -d debug_level           Debug level: 0..7
HELP
    exit(1)
}

#-------------------------------------------------------------------------
# Check command line options.
#-------------------------------------------------------------------------
$debug = $opt_d if (($opt_d >= $LOG_EMERG) and ($opt_d <= $LOG_DEBUG));
$port  = $opt_p if (defined($opt_p));
$opt_f = substr($opt_f, 0, -4) if (substr($opt_f, -4) eq '.bin');
$opt_b = substr($opt_b, 0, -4) if (substr($opt_b, -4) eq '.bin');


#-------------------------------------------------------------------------
# Do not open the device, read instead an existing binary log file.
#-------------------------------------------------------------------------
if ($opt_b) {
    if ($opt_t or $opt_w) {
        # Parse binary data and save GPX files.
        $opt_f = $opt_b;
        # Total number of records in unknown: we will exit on error.
        $number_of_records = 0xffffffff;
        parse_log_data();
    }
    exit;
}

#-------------------------------------------------------------------------
# Initialize the device port.
#-------------------------------------------------------------------------
$ob = Device::SerialPort->new ($port);

$ob->baudrate(115200)   || die "fail setting parity";
$ob->parity('none')     || die "fail setting parity";
$ob->databits(8)        || die "fail setting databits";
$ob->stopbits(1)        || die "fail setting stopbits";
$ob->handshake('none')  || die "fail setting handshake";
$ob->write_settings     || die "no settings";

# Send test packet (PMTK_TEST).
packet_send('PMTK000');
packet_wait('PMTK001,0,');
print "MTK Test OK\n";

# Query firmware version (PMTK_Q_VERSION).
packet_send('PMTK604');
$ret = packet_wait('PMTK001,604,');
if ($ret =~ m/PMTK001,604,([0-9A-Za-z]+)\*/) {
    $version = $1;
}

# Query firmware release (PMTK_Q_RELEASE).
packet_send('PMTK605');
$ret = packet_wait('PMTK705,');
if ($ret =~ m/PMTK705,([\.0-9A-Za-z_-]+),([0-9A-Za-z]+)\*/) {
    $release  = $1;
    $model_id = $2;
}

printf "MTK Firmware: Version: $version, Release: $release, Model ID: $model_id\n";

#-------------------------------------------------------------------------
# Erase memory.
#-------------------------------------------------------------------------
if ($opt_E) {
    printf(">> Erasing log memory...\n");
    packet_send('PMTK182,6,1');
    packet_wait('PMTK001,182,6,3', 20);
}

#-------------------------------------------------------------------------
# Turn ON or OFF data logging.
#-------------------------------------------------------------------------
if ($opt_l eq 'on') {
    printf(">> Switch recording to ON\n");
    # Send PMTK_LOG_ON.
    packet_send('PMTK182,4');
    packet_wait('PMTK001,182,4,3');
}
if ($opt_l eq 'off') {
    printf(">> Switch recording to OFF\n");
    # Send PMTK_LOG_OFF.
    packet_send('PMTK182,5');
    packet_wait('PMTK001,182,5,3');
}

#-------------------------------------------------------------------------
# Set recording triggers: TIME, DISTANCE, SPEED.
#-------------------------------------------------------------------------
if ($opt_r) {
    printf(">> Setting recording triggers: time, distance, speed\n");
    my ($time, $distance, $speed) = split(/:/, $opt_r);
    $time     = int($time    ) if (defined($time));
    $distance = int($distance) if (defined($distance));
    $speed    = int($speed   ) if (defined($speed));
    if (($time >= 1 and $time <= 999) or ($time == 0)) {
        packet_send(sprintf('PMTK182,1,3,%u', $time * 10));
        packet_wait('PMTK001,182,1,3');
    }
    if (($distance >= 10 and $distance <= 9999) or ($distance == 0)) {
        packet_send(sprintf('PMTK182,1,4,%u', $distance * 10));
        packet_wait('PMTK001,182,1,3');
    }
    if (($speed >= 10 and $speed <= 999) or ($speed == 0)) {
        packet_send(sprintf('PMTK182,1,5,%u', $speed * 10));
        packet_wait('PMTK001,182,1,3');
    }
}

#-------------------------------------------------------------------------
# Set recording method: OVERWRITE or STOP (PMTK_LOG_REC_METHOD).
#-------------------------------------------------------------------------
if ((lc($opt_m) eq 'overwrite') or (lc($opt_m) eq 'stop')) {
    if (lc($opt_m) eq 'overwrite') {
        printf(">> Setting method OVERWRITE on memory full\n");
        packet_send('PMTK182,1,6,1');
    } else {
        printf(">> Setting method STOP on memory full\n");
        packet_send('PMTK182,1,6,2');
    }
    $ret = packet_wait('PMTK001,182,1,');
    if ($ret =~ m/PMTK001,182,1,(\d)/) {
        if ($1 ne '3') {
            printf(">> ERROR: cannot set recording method\n");
        }
    }
}

#-------------------------------------------------------------------------
# Set log format (PMTK_LOG_SETFORMAT).
#-------------------------------------------------------------------------
if ($opt_o) {
    printf("Setting log format\n");
    # Get current log format.
    packet_send('PMTK182,2,2');
    $ret = packet_wait('PMTK182,3,2,');
    packet_wait('PMTK001,182,2,3');
    if ($ret =~ m/PMTK182,3,2,([0-9A-Za-z]+)\*/) {
        $log_format = hex($1);
        $log_format = encode_log_format($log_format, $opt_o);
        packet_send(sprintf('PMTK182,1,2,%08X', $log_format));
        $ret = packet_wait('PMTK001,182,1,3');
    }
}


# Query log format (PMTK_LOG_QUERY).
packet_send('PMTK182,2,2');
$ret = packet_wait('PMTK182,3,2,');
packet_wait('PMTK001,182,2,3');
if ($ret =~ m/PMTK182,3,2,([0-9A-Za-z]+)\*/) {
    $log_format = hex($1);
    printf("Log format: (%s) %s = %u bytes\n", $1, describe_log_format($log_format), sizeof_log_format($log_format) + 2);
}

# Query recording triggers: time, distance, speed (PMTK_LOG_QUERY).
packet_send('PMTK182,2,3');
$ret = packet_wait('PMTK182,3,3,');
if ($ret =~ m/PMTK182,3,3,([0-9]+)\*/) {
    printf("Logging TIME interval:     %6.2f s\n", $1 / 10);
}
packet_send('PMTK182,2,4');
$ret = packet_wait('PMTK182,3,4,');
if ($ret =~ m/PMTK182,3,4,([0-9]+)\*/) {
    printf("Logging DISTANCE interval: %6.2f m\n", $1 / 10);
}
packet_send('PMTK182,2,5');
$ret = packet_wait('PMTK182,3,5,');
if ($ret =~ m/PMTK182,3,5,([0-9]+)\*/) {
    printf("Logging SPEED limit:       %6.2f km/h\n", $1 / 10);
}

# Query recording method (OVERWRITE/STOP).
packet_send('PMTK182,2,6');
packet_wait('PMTK001,182,2,3');
$ret = packet_wait('PMTK182,3,6,');
if ($ret =~ m/PMTK182,3,6,([0-9]+)\*/) {
    printf("Recording method on memory full: %s (%u)\n", describe_recording_method($1), $1);
}

# Query recording status (OFF/OFF).
packet_send('PMTK182,2,7');
packet_wait('PMTK001,182,2,3');
$ret = packet_wait('PMTK182,3,7,');
if ($ret =~ m/PMTK182,3,7,([0-9A-Za-z]+)\*/) {
    printf("Recording status: %s (%09b)\n", describe_recording_status($1), $1);
}

# Query memory used by the stored log.
packet_send('PMTK182,2,8');
$ret = packet_wait('PMTK182,3,8,');
packet_wait('PMTK001,182,2,3');
if ($ret =~ m/PMTK182,3,8,([0-9A-Za-z]+)\*/) {
    $memory_used = hex($1);
    printf("Memory used (without block headers): %u\n", $memory_used);
}

# Query number of records stored in the log.
packet_send('PMTK182,2,10');
$ret = packet_wait('PMTK182,3,10,');
packet_wait('PMTK001,182,2,3');
if ($ret =~ m/PMTK182,3,10,([0-9A-Za-z]+)\*/) {
    $number_of_records = hex($1);
    printf("Number of records: %u\n", $number_of_records);
}

#-------------------------------------------------------------------------
# Get binary data from the device and save to a file.
#-------------------------------------------------------------------------
if ($opt_f and ($opt_t or $opt_w)) {

    # Compute the actual memory used by data log.
    # For each block we have a block header.
    $blocks  = int($memory_used / $SIZEOF_BLOCK);
    $blocks += 1 if (($memory_used % $SIZEOF_BLOCK) != 0);
    $memory_used = $blocks * ($SIZEOF_BLOCK + $SIZEOF_BLOCK_HEADER);
    # Size of data to read, round-up size to the entire chunk.
    $bytes_to_read  = int($memory_used / $SIZEOF_CHUNK) * $SIZEOF_CHUNK;
    $bytes_to_read += $SIZEOF_CHUNK if (($memory_used % $SIZEOF_CHUNK) != 0);
    printf(">> Retrieving log data from device: %u bytes...\n", $bytes_to_read);

    open($fp_log, ">${opt_f}.bin") or die("Cannot open file ${opt_f}.bin: $!");

    # NOTE: On a slow machine there was some problem getting the entire log data
    # via USB port with a single PMTK_LOG_REQ_DATA request.
    # The GPS device eventually begins to send packets longer than $SIZEOF_CHUNK,
    # apparently with corrupted data (failed checksum).

    # To be safe we iterate requesting $SIZEOF_CHUNK at time.
    for ($offset = 0; $offset < $bytes_to_read; $offset += $SIZEOF_CHUNK) {
        # Request log data (PMTK_LOG_REQ_DATA) from $offset to $bytes_to_read.
        packet_send(sprintf('PMTK182,7,%08X,%08X', $offset, $SIZEOF_CHUNK));
        # Start writing binary data to file and wait the final PMTK_ACK packet.
        packet_wait('PMTK001,182,7,3', 10);
    }
    close($fp_log);
    undef($fp_log);

    # Parse binary data and save GPX files.
    parse_log_data();

}

$ob->close || warn "close failed";

exit;

#-------------------------------------------------------------------------
# Calculate the packet checksum: bitwise XOR of string's bytes.
#-------------------------------------------------------------------------
sub packet_checksum {

    my $pkt   = shift;
    my $len   = length($pkt);
    my $check = 0;
    my $i;

    for ($i = 0; $i < $len; $i++) { $check ^= ord(substr($pkt, $i, 1)); }
    return($check);
}

#-------------------------------------------------------------------------
# Send NMEA packet to the device.
#-------------------------------------------------------------------------
sub packet_send {

    my $pkt = shift;
    my $n;

    # Add the checksum to the packet.
    $pkt = $pkt . '*' . sprintf('%02X', packet_checksum($pkt));
    printf("%s TX packet => %s\n", log_time(), $pkt) if ($debug >= $LOG_NOTICE);
    # Add the preamble and <CR><LF>.
    $pkt = '$' . $pkt . "\r\n";

    $n = $ob->write($pkt);
    printf("Writing %u bytes to device; actually written %u bytes\n", length($pkt), $n) if ($debug >= $LOG_DEBUG);
    die("ERROR: Writing to device: $!") if ($n != length($pkt));
}

#-------------------------------------------------------------------------
# Read a packet from the device.
# Return the packet with PktType, DataField, "*" and Checksum.
#
#   Example: PMTK182,3,8,0004E69C*13
#
# The packet received has a leading Preample and a trailing <CR><LF>,
# example: $PMTK182,3,8,0004E69C*13<CR><LF>
#-------------------------------------------------------------------------
sub packet_read {

    my $timeout = shift;
    my $pkt;
    my $c;
    my $n;
    my $previous_c;
    my $payload;
    my $checksum;

    # Timeout (in milliseconds) for activity on the port.
    $timeout = $TIMEOUT_IDLE_PORT if (!defined($timeout));
    $ob->read_const_time($timeout);

    # Wait packet preamble.
    while ($c ne '$') {
        ($n, $c) = $ob->read(1);
        die("ERROR: Reading from device: $!") if ($n != 1);
    }

    $pkt = '';
    $previous_c = '';

    # Read until End Of Packet.
    while (1) {
        ($n, $c) = $ob->read(1);
        die("ERROR: Reading from device: $!") if ($n != 1);
        if ($c eq '$') {
            $pkt = '';
        } else {
            $pkt .= $c;
        }
        if (($c eq "\n") and ($previous_c eq "\r")) {
            last;
        }
        $previous_c = $c;
    }

    # Remove trailing <CR><LF>.
    $pkt = substr($pkt, 0, -2);
    printf("%s RX packet <= %s\n", log_time(), $pkt) if ($debug >= $LOG_NOTICE);

    # Extract packet payload and checksum.
    $payload  = substr($pkt,  0, -3);
    $checksum = hex(substr($pkt, -2,  2));

    # Verify packet checksum.
    if ($checksum ne packet_checksum($payload)) {
        printf("Packet checksum error: expected 0x%02X, computed 0x%02X\n", $checksum, packet_checksum($payload)) if ($debug >= $LOG_ERR);
        return('');
    } else {
        return($pkt);
    }
}

#-------------------------------------------------------------------------
# Read packets from the device, untill we get the one we want.
#-------------------------------------------------------------------------
sub packet_wait {

    my $pkt_type = shift;
    my $timeout  = shift;
    my $max_time;
    my $pkt;
    my $len;
    my $i;

    $len = length($pkt_type);

    # Timeout (in seconds) for packet wait.
    $timeout = $TIMEOUT if (!defined($timeout));
    $max_time = time() + $timeout;

    while(1) {
        $pkt = packet_read($timeout * 1000);
        return($pkt) if (substr($pkt, 0, $len) eq $pkt_type);
        write_log_packet($pkt) if (defined($fp_log));
        last if (time() > $max_time);
    }
    printf("%s ERROR: packet_wait() failed for packet %s\n", log_time(), $pkt_type) if ($debug >= $LOG_ERR);
    return(undef);
}

#-------------------------------------------------------------------------
# Append log data packets to a file.
#
# Packet format is PMTK182,8,SSSSSSSS,PacketData*CC where:
#   SSSSSSSS   = Offset of first byte (hex value, 8 chars)
#   PacketData = Packet data (hex values, 4096 chars)
#   *          = Separator (1 char)
#   CC         = Checksum of data (hex value, 2 chars)
#
# TODO: Remove hard coded limit to packet length.
#
#-------------------------------------------------------------------------
sub write_log_packet {

    my $pkt = shift;
    my $pkt_len;
    my $pkt_offset;
    my $log_offset;
    my $percent;
    my $i;

    # Save only datalog packets (PMTK_LOG_RESP_DATA).
    return if (substr($pkt, 0, 10) ne 'PMTK182,8,');

    # Check if packet is length is ok and if chunk is in sequence order.
    $log_offset = tell($fp_log);
    $pkt_offset = hex(substr($pkt, 10, 8));
    $pkt_len    = ($SIZEOF_CHUNK * 2) + 22;
    if ($pkt_offset != $log_offset) {
        printf("ERROR: chunk out of sequence: expected %08X, got %08X\n", $log_offset, $pkt_offset) if ($debug >= $LOG_ERR);
    } elsif (length($pkt) != $pkt_len) {
        printf("ERROR: packet size error: expected %04X, got %04X\n", $pkt_len, length($pkt)) if ($debug >= $LOG_ERR);
    } else {
        printf("Saving log data, offset: 0x%08X\n", $log_offset) if ($debug >= $LOG_INFO);
        # Convert the string with hex values into binary data.
        for ($i = 19; $i < ($pkt_len - 3); $i += 2) {
            printf $fp_log chr(hex(substr($pkt, $i, 2)));
        }
        $percent = ($log_offset / $bytes_to_read) * 100;
        printf("Saved log data: %6.2f%%\n", $percent);
    }
}

#-------------------------------------------------------------------------
# Parse log data in raw binary format and write GPX files.
#-------------------------------------------------------------------------
sub parse_log_data {

    my $i;
    my $fp;
    my $fp_gpx;
    my $fp_gpx_trk;
    my $fp_gpx_wpt;
    my $buffer;
    my $log_len;
    my $record_count;
    my $count_tot;
    my $records_in_block;
    my $log_format;
    my $checksum;
    my $pad_len;
    my $gpx_in_trk = 0;
    
    my $gpx_trk_fname     = "${opt_f}_trk.gpx";
    my $gpx_wpt_fname     = "${opt_f}_wpt.gpx";
    my $gpx_trk_tmp_fname = "${opt_f}_trk.gpx.$$.tmp";
    my $gpx_wpt_tmp_fname = "${opt_f}_wpt.gpx.$$.tmp";

    # Open the binary log file for reading.
    open($fp, "${opt_f}.bin") or die("ERROR reading ${opt_f}.bin: $!");
    seek($fp, 0, 2) or die;
    $log_len = tell($fp);
    seek($fp, 0, 0) or die;

    # Open GPX temporary files for writing tracks and waypoints.
    open($fp_gpx_trk, ">$gpx_trk_tmp_fname") or die("ERROR writing $gpx_trk_tmp_fname: $!") if ($opt_t);
    open($fp_gpx_wpt, ">$gpx_wpt_tmp_fname") or die("ERROR writing $gpx_wpt_tmp_fname: $!") if ($opt_w);

    while (1) {

        # Print current file position.
        printf("%08X\n", tell($fp)) if ($debug >= $LOG_INFO);

        #-----------------------------------------
        # Process the begin of a block.
        #-----------------------------------------
        if ((tell($fp) % $SIZEOF_BLOCK) == 0) {
            # Reached the begin of a log block (every 0x10000 bytes). Get the header (0x200 bytes).
            $buffer = my_read($fp, $SIZEOF_BLOCK_HEADER);
            # What we will find in this log block:
            ($records_in_block, $log_format) = parse_block_header($buffer);
	    last if (!defined($records_in_block));
            $record_count = 0;
        }

        #-----------------------------------------
        # Check if there is a record separator:
        # AAAAAAAAAAAAAA XX YYYYYYYY BBBBBBBB
        #-----------------------------------------
        if (($log_len - tell($fp)) >= $SIZEOF_SEPARATOR) {
            $buffer = my_read($fp, $SIZEOF_SEPARATOR);
            if ((substr($buffer, 0, 7) ne (chr(0xaa) x 7)) or (substr($buffer, -4) ne (chr(0xbb) x 4))) {
                # Not a record separator: rewind the file pointer.
                seek($fp, -$SIZEOF_SEPARATOR, 1);
            } else {
                # Found a record separator.
                my $separator_type = ord(substr($buffer, 7, $SIZEOF_BYTE));
                my $separator_arg  = mtk2long(substr($buffer, 8, $SIZEOF_LONG));
                printf("Separator: %s, type: %s\n", uc(unpack('H*', $buffer)), describe_separator_type($separator_type)) if ($debug >= $LOG_INFO);
                if ($separator_type == $SEP_TYPE_CHANGE_LOG_BITMASK) {
                    $log_format = $separator_arg;
                    printf("New log bitmask: %s (0x%08X = %s)\n", $separator_arg, $log_format, describe_log_format($log_format)) if ($debug >= $LOG_INFO);
                }
                # Close the current <trk> in GPX.
                if ($gpx_in_trk) {
                    gpx_print_trk_end($fp_gpx_trk) if ($opt_t);
                    $gpx_in_trk = 0;
                }
                next; # Search for the next record or record separator.
            }
        }

        #-----------------------------------------
        # Check if all the records has been read.
        #-----------------------------------------
        if ($record_count >= $records_in_block or $count_tot >= $number_of_records) {
            # All the records in this chunk has been read, the rest of the chunk should be padding bytes (0xFF).
            $pad_len = $SIZEOF_CHUNK - (tell($fp) % $SIZEOF_CHUNK);
            $buffer = my_read($fp, $pad_len);
            printf("Padding: %s\n", uc(unpack('H*', $buffer))) if ($debug >= $LOG_DEBUG);
            if ($buffer ne (chr(0xff) x $pad_len)) {
                printf("ERROR: Invalid pad string\n");
                last;
            }
            if ($count_tot >= $number_of_records) {
                printf("Total record count: %u\n", $count_tot);
                # Record count in the last block may be unfinalized (0xffff).
                if (($record_count < $records_in_block) and ($records_in_block != 0xffff)) {
                    printf("ERROR: Invalid record count in last block: 0x%04X (should be 0x%04X)\n", $records_in_block, 0xffff) if ($debug >= $LOG_ERR);
                }
                last;
            } else {
                next; # Search for the next record or record separator.
            }
        }

        #-----------------------------------------
        # Read a log record.
        #-----------------------------------------
        $record_count++;
        $count_tot++;
        $checksum = 0;
        printf("Reading log block: record %u (%u/%u total)\n", $record_count, $count_tot, $number_of_records) if ($debug >= $LOG_INFO);

        # Read each record field.
        undef($record_utc);
        if ($log_format & $LOG_FORMAT_UTC) {
            $buffer = my_read($fp, $SIZEOF_LOG_UTC);
            $checksum ^= packet_checksum($buffer);
            $record_utc = mtk2time($buffer);
            printf("Record UTC: %s %s\n", uc(unpack('H*', $buffer)), $record_utc) if ($debug >= $LOG_DEBUG);
        }

        undef($record_valid);
        if ($log_format & $LOG_FORMAT_VALID) {
            $buffer = my_read($fp, $SIZEOF_LOG_VALID);
            $checksum ^= packet_checksum($buffer);
            $record_valid = mtk2word($buffer);
            printf("Record VALID: %s (0x%04X = %s)\n", uc(unpack('H*', $buffer)), $record_valid, describe_valid_field($record_valid)) if ($debug >= $LOG_DEBUG);
         }

        undef($record_latitude);
        if ($log_format & $LOG_FORMAT_LATITUDE) {
            $buffer = my_read($fp, $SIZEOF_LOG_LATITUDE);
            $checksum ^= packet_checksum($buffer);
            $record_latitude = mtk2double($buffer);
            printf("Record LATITUDE: %s (%.9f)\n", uc(unpack('H*', $buffer)), $record_latitude) if ($debug >= $LOG_DEBUG);
        }

        undef($record_longitude);
        if ($log_format & $LOG_FORMAT_LONGITUDE) {
            $buffer = my_read($fp, $SIZEOF_LOG_LONGITUDE);
            $checksum ^= packet_checksum($buffer);
            $record_longitude = mtk2double($buffer);
            printf("Record LONGITUDE: %s (%.9f)\n", uc(unpack('H*', $buffer)), $record_longitude) if ($debug >= $LOG_DEBUG);
        }

        undef($record_height);
        if ($log_format & $LOG_FORMAT_HEIGHT) {
            $buffer = my_read($fp, $SIZEOF_LOG_HEIGHT);
            $checksum ^= packet_checksum($buffer);
            $record_height = mtk2float($buffer);
            printf("Record HEIGHT: %s (%.6f)\n", uc(unpack('H*', $buffer)), $record_height) if ($debug >= $LOG_DEBUG);
        }

        undef($record_speed);
        if ($log_format & $LOG_FORMAT_SPEED) {
            $buffer = my_read($fp, $SIZEOF_LOG_SPEED);
            $checksum ^= packet_checksum($buffer);
            $record_speed = mtk2float($buffer);
            printf("Record SPEED: %s (%.6f)\n", uc(unpack('H*', $buffer)), $record_speed) if ($debug >= $LOG_DEBUG);
        }

        undef($record_heading);
        if ($log_format & $LOG_FORMAT_HEADING) {
            $buffer = my_read($fp, $SIZEOF_LOG_HEADING);
            $checksum ^= packet_checksum($buffer);
            $record_heading = mtk2float($buffer);
            printf("Record HEADING: %s (%.6f)\n", uc(unpack('H*', $buffer)), $record_heading) if ($debug >= $LOG_DEBUG);
        }

        undef($record_dsta);
        if ($log_format & $LOG_FORMAT_DSTA) {
            $buffer = my_read($fp, $SIZEOF_LOG_DSTA);
            $checksum ^= packet_checksum($buffer);
            $record_dsta = mtk2word($buffer);
            printf("Record DSTA: %s (%u)\n", uc(unpack('H*', $buffer)), $record_dsta) if ($debug >= $LOG_DEBUG);
        }

        undef($record_dage);
        if ($log_format & $LOG_FORMAT_DAGE) {
            $buffer = my_read($fp, $SIZEOF_LOG_DAGE);
            $checksum ^= packet_checksum($buffer);
            $record_dage = mtk2long($buffer);
            printf("Record DAGE: %s (%u)\n", uc(unpack('H*', $buffer)), $record_dage) if ($debug >= $LOG_DEBUG);
        }

        undef($record_pdop);
        if ($log_format & $LOG_FORMAT_PDOP) {
            $buffer = my_read($fp, $SIZEOF_LOG_PDOP);
            $checksum ^= packet_checksum($buffer);
            $record_pdop = mtk2word($buffer) / 100;
            printf("Record PDOP: %s (%.2f)\n", uc(unpack('H*', $buffer)), $record_pdop) if ($debug >= $LOG_DEBUG);
        }

        undef($record_hdop);
        if ($log_format & $LOG_FORMAT_HDOP) {
            $buffer = my_read($fp, $SIZEOF_LOG_HDOP);
            $checksum ^= packet_checksum($buffer);
            $record_hdop = mtk2word($buffer) / 100;
            printf("Record HDOP: %s (%.2f)\n", uc(unpack('H*', $buffer)), $record_hdop) if ($debug >= $LOG_DEBUG);
        }

        undef($record_vdop);
        if ($log_format & $LOG_FORMAT_VDOP) {
            $buffer = my_read($fp, $SIZEOF_LOG_VDOP);
            $checksum ^= packet_checksum($buffer);
            $record_vdop = mtk2word($buffer) / 100;
            printf("Record VDOP: %s (%.2f)\n", uc(unpack('H*', $buffer)), $record_vdop) if ($debug >= $LOG_DEBUG);
        }

        undef($record_nsat_in_use);
        undef($record_nsat_in_view);
        if ($log_format & $LOG_FORMAT_NSAT) {
            $buffer = my_read($fp, $SIZEOF_BYTE);
            $checksum ^= packet_checksum($buffer);
            $record_nsat_in_view = mtk2byte($buffer);
            $buffer = my_read($fp, $SIZEOF_BYTE);
            $checksum ^= packet_checksum($buffer);
            $record_nsat_in_use = mtk2byte($buffer);
            printf("Record NSAT: in view %u, in use %u\n", $record_nsat_in_view, $record_nsat_in_use) if ($debug >= $LOG_DEBUG);

            # Logging satellite info, but no satellite in view: skip the following empty word.
            if (($log_format & $LOG_FORMAT_SID) and ($record_nsat_in_view == 0)) {
                $buffer = my_read($fp, $SIZEOF_LONG);
                $checksum ^= packet_checksum($buffer);
                printf("Record NO_SAT_DATA: %s\n", uc(unpack('H*', $buffer))) if ($debug >= $LOG_DEBUG);
            }

            # Read data for each satellite.
            for ($i = 0; $i < $record_nsat_in_view; $i++) {

                my $record_nsat_sid;
                my $record_nsat_sidinuse;
                my $record_nsat_nmbrsats;
                my $record_nsat_elevation;
                my $record_nsat_azimuth;
                my $record_nsat_snr;

                undef($record_nsat_sid);
                undef($record_nsat_sidinuse);
                undef($record_nsat_nmbrsats);
                if ($log_format & $LOG_FORMAT_SID) {
                    $buffer = my_read($fp, $SIZEOF_LOG_SID);
                    $checksum ^= packet_checksum($buffer);
                    $record_nsat_sid = ord($buffer);
                    $buffer = my_read($fp, $SIZEOF_LOG_SIDINUSE);
                    $checksum ^= packet_checksum($buffer);
                    $record_nsat_sidinuse = ord($buffer);
                    $buffer = my_read($fp, $SIZEOF_LOG_NBRSATS);
                    $checksum ^= packet_checksum($buffer);
                    $record_nsat_nmbrsats = unpack('S', $buffer);
                    printf("Satellite %u: ID = %u, In Use %u, Nbr = 0x%04X\n", $i, $record_nsat_sid,
                        $record_nsat_sidinuse, $record_nsat_nmbrsats) if ($debug >= $LOG_DEBUG);
                }
                undef($record_nsat_elevation);
                if ($log_format & $LOG_FORMAT_ELEVATION) {
                    $buffer = my_read($fp, $SIZEOF_LOG_ELEVATION);
                    $checksum ^= packet_checksum($buffer);
                    $record_nsat_elevation = mtk2word($buffer);
                    printf("Satellite ELEVATION: %u\n", $record_nsat_elevation) if ($debug >= $LOG_DEBUG);
                }
                undef($record_nsat_azimuth);
                if ($log_format & $LOG_FORMAT_AZIMUTH) {
                    $buffer = my_read($fp, $SIZEOF_LOG_AZIMUTH);
                    $checksum ^= packet_checksum($buffer);
                    $record_nsat_azimuth = mtk2word($buffer);
                    printf("Satellite AZIMUTH: %u\n", $record_nsat_azimuth) if ($debug >= $LOG_DEBUG);
                }
                undef($record_nsat_snr);
                if ($log_format & $LOG_FORMAT_SNR) {
                    $buffer = my_read($fp, $SIZEOF_LOG_SNR);
                    $checksum ^= packet_checksum($buffer);
                    $record_nsat_snr = mtk2word($buffer);
                    printf("Satellite SNR: %u\n", $record_nsat_snr) if ($debug >= $LOG_DEBUG);
                }
            }

        }

        undef($record_rcr);
        if ($log_format & $LOG_FORMAT_RCR) {
            $buffer = my_read($fp, $SIZEOF_LOG_RCR);
            $checksum ^= packet_checksum($buffer);
            $record_rcr = mtk2word($buffer);
            printf("Record RCR: %s (%s)\n", uc(unpack('H*', $buffer)), describe_rcr_field($record_rcr)) if ($debug >= $LOG_DEBUG);
        }

        undef($record_millisecond);
        if ($log_format & $LOG_FORMAT_MILLISECOND) {
            $buffer = my_read($fp, $SIZEOF_LOG_MILLISECOND);
            $checksum ^= packet_checksum($buffer);
            $record_millisecond = mtk2word($buffer);
            printf("Record MILLISECOND: %s (%u)\n", uc(unpack('H*', $buffer)), $record_millisecond) if ($debug >= $LOG_DEBUG);
        }

        undef($record_distance);
        if ($log_format & $LOG_FORMAT_DISTANCE) {
            $buffer = my_read($fp, $SIZEOF_LOG_DISTANCE);
            $checksum ^= packet_checksum($buffer);
            $record_distance = mtk2double($buffer);
            printf("Record DISTANCE: %s (%.9f)\n", uc(unpack('H*', $buffer)), $record_distance) if ($debug >= $LOG_DEBUG);
        }

        # Read and verify checksum.
        $buffer = my_read($fp, $SIZEOF_BYTE);
        if ($buffer ne '*') {
            printf("ERROR: Checksum separator error: expected char 0x%02X, found 0x%02X\n", ord('*'), ord($buffer));
            last;
        }
        $buffer = my_read($fp, $SIZEOF_BYTE);
        if ($checksum != ord($buffer)) {
            printf("ERROR: Record checksum error: expected 0x%02X, computed 0x%02X\n", ord($buffer), $checksum);
            last;
        }

        # Start a new GPX <trkseg> on satellite lost.
        if (($record_valid == $VALID_NO_FIX) and $gpx_in_trk) {
            gpx_print_trk_end($fp_gpx_trk) if ($opt_t);
            $gpx_in_trk = 0;
        }

        # Write <trkpt> data in GPX file.
        if (($record_valid != $VALID_NO_FIX) and !($record_rcr & $RCR_INTEREST) and defined($record_latitude) and defined($record_longitude)) {
            if (! $gpx_in_trk) {
                gpx_print_trk_begin($fp_gpx_trk) if ($opt_t);
                $gpx_in_trk = 1;
            }
            gpx_print_trkpt($fp_gpx_trk) if ($opt_t);
        }
        # Write <wpt> data in GPX file.
        if (($record_rcr & $RCR_INTEREST) and ($record_valid != $VALID_NO_FIX)) {
            gpx_print_wpt($fp_gpx_wpt) if ($opt_w);
        }

    }
    close($fp);

    # Eventually close the <trk> GPX tags.
    if ($gpx_in_trk) {
        gpx_print_trk_end($fp_gpx_trk) if ($opt_t);
        $gpx_in_trk = 0;
    }

    # Write GPX tracks file.
    if ($opt_t) {
        close($fp_gpx_trk);
        open($fp_gpx, ">$gpx_trk_fname") or die("ERROR writing $gpx_trk_fname: $!");
        gpx_print_gpx_begin($fp_gpx, time(), $gpx_trk_minlat, $gpx_trk_minlon, $gpx_trk_maxlat, $gpx_trk_maxlon);
        open($fp_gpx_trk, "$gpx_trk_tmp_fname") or die;
        while (<$fp_gpx_trk>) { print $fp_gpx $_; }
        close($fp_gpx_trk);
        gpx_print_gpx_end($fp_gpx);
        close($fp_gpx);
        unlink($gpx_trk_tmp_fname);
    }

    # Write GPX waypoints file.
    if ($opt_w) {
        close($fp_gpx_wpt);
        open($fp_gpx, ">$gpx_wpt_fname") or die("ERROR writing $gpx_wpt_fname: $!");
        gpx_print_gpx_begin($fp_gpx, time(), $gpx_wpt_minlat, $gpx_wpt_minlon, $gpx_wpt_maxlat, $gpx_wpt_maxlon);
        open($fp_gpx_wpt, "$gpx_wpt_tmp_fname") or die;
        while (<$fp_gpx_wpt>) { print $fp_gpx $_; }
        close($fp_gpx_wpt);
        gpx_print_gpx_end($fp_gpx);
        close($fp_gpx);
        unlink($gpx_wpt_tmp_fname);
    }
}

#-------------------------------------------------------------------------
# Read some bytes from the device and return them.
#-------------------------------------------------------------------------
sub my_read {
    my $handle   = shift;
    my $length   = shift;
    my $variable;
    my $n = read($handle, $variable, $length);
    printf("ERROR: Reading file: read %u bytes, expected %u\n", $n, $length) if ($n != $length);
    return($variable);
}

#-------------------------------------------------------------------------
# Parse the header (0x200 bytes) of a datalog block (every 0x10000 bytes).
# Return the number of records in the block and the log format.
#-------------------------------------------------------------------------
sub parse_block_header {

    my $block_header = shift;

    if ($debug >= $LOG_NOTICE) {
        printf("%s Block header\n", '=' x 20);
        printf("Block header: %s\n", uc(unpack('H*', $block_header))) if ($debug >= $LOG_DEBUG);
    }

    # Check validity of block header.
    my $separator   =     substr($block_header, -6, 1);              # Should be '*'
    my $checksum    = ord(substr($block_header, -5, $SIZEOF_BYTE));  # ??? It's not the XOR checksum
    my $header_tail =     substr($block_header, -4, 4);              # Should be 0xBBBBBBBB
    if (($separator ne '*') or ($header_tail ne (chr(0xBB) x 4))) {
        printf("ERROR: Invalid datalog block header\n");
	return(undef(), undef());
    }

    # Settings of this log block (hex values, LSB first).
    my $log_count    = substr($block_header,  0, $SIZEOF_WORD); # Records in this block. 0xFFFF if the block is not filled.
    my $log_format   = substr($block_header,  2, $SIZEOF_LONG); # Log format bitmask
    my $log_bo       = substr($block_header,  6, $SIZEOF_WORD); # ???
    my $log_period   = substr($block_header,  8, $SIZEOF_LONG); # Log period   in 10ths of s
    my $log_distance = substr($block_header, 12, $SIZEOF_LONG); # Log distance in 10ths of m
    my $log_speed    = substr($block_header, 16, $SIZEOF_LONG); # Log speed    in 10ths ok km/h

    my $count    = unpack('S', $log_count);
    my $format   = unpack('L', $log_format);
    my $bo       = unpack('S', $log_bo);
    my $period   = unpack('L', $log_period);
    my $distance = unpack('L', $log_distance);
    my $speed    = unpack('L', $log_speed);

    if ($debug >= $LOG_NOTICE) {
        printf ("Log count:    %s %s %u records\n",  uc(unpack('H*', $log_count)),    ' 'x10, $count);
        printf ("Log bitmask:  %s %s 0x%08X (%s)\n", uc(unpack('H*', $log_format)),   ' 'x6,  $format, describe_log_format($format));
        printf ("Log ???:      %s %s 0x%04X\n",      uc(unpack('H*', $log_bo)),       ' 'x10, $bo);
        printf ("Log period:   %s %s %3d s\n",       uc(unpack('H*', $log_period)),   ' 'x6,  $period   / 10);
        printf ("Log distance: %s %s %3d m\n",       uc(unpack('H*', $log_distance)), ' 'x6,  $distance / 10);
        printf ("Log speed:    %s %s %3d km/h\n",    uc(unpack('H*', $log_speed)),    ' 'x6,  $speed    / 10);
    }

    return($count, $format);
}

#-------------------------------------------------------------------------
# Given a log format value (bitmask), return a description string.
#-------------------------------------------------------------------------
sub describe_log_format {

    my $log_format = shift;
    my $str = '';

    $str .=  ',UTC'        if ($log_format & $LOG_FORMAT_UTC);
    $str .= ',VALID'       if ($log_format & $LOG_FORMAT_VALID);
    $str .= ',LATITUDE'    if ($log_format & $LOG_FORMAT_LATITUDE);
    $str .= ',LONGITUDE'   if ($log_format & $LOG_FORMAT_LONGITUDE);
    $str .= ',HEIGHT'      if ($log_format & $LOG_FORMAT_HEIGHT);
    $str .= ',SPEED'       if ($log_format & $LOG_FORMAT_SPEED);
    $str .= ',HEADING'     if ($log_format & $LOG_FORMAT_HEADING);
    $str .= ',DSTA'        if ($log_format & $LOG_FORMAT_DSTA);
    $str .= ',DAGE'        if ($log_format & $LOG_FORMAT_DAGE);
    $str .= ',PDOP'        if ($log_format & $LOG_FORMAT_PDOP);
    $str .= ',HDOP'        if ($log_format & $LOG_FORMAT_HDOP);
    $str .= ',VDOP'        if ($log_format & $LOG_FORMAT_VDOP);
    $str .= ',NSAT'        if ($log_format & $LOG_FORMAT_NSAT);
    $str .= ',SID'         if ($log_format & $LOG_FORMAT_SID);
    $str .= ',ELEVATION'   if ($log_format & $LOG_FORMAT_ELEVATION);
    $str .= ',AZIMUTH'     if ($log_format & $LOG_FORMAT_AZIMUTH);
    $str .= ',SNR'         if ($log_format & $LOG_FORMAT_SNR);
    $str .= ',RCR'         if ($log_format & $LOG_FORMAT_RCR);
    $str .= ',MILLISECOND' if ($log_format & $LOG_FORMAT_MILLISECOND);
    $str .= ',DISTANCE'    if ($log_format & $LOG_FORMAT_DISTANCE);

    return(substr($str, 1));
}

#-------------------------------------------------------------------------
#
#-------------------------------------------------------------------------
sub encode_log_format {

    my $log_format = shift;
    my $changes = shift;

    $log_format |= $LOG_FORMAT_UTC         if ($changes =~ m/\+UTC\b/);
    $log_format |= $LOG_FORMAT_VALID       if ($changes =~ m/\+VALID\b/);
    $log_format |= $LOG_FORMAT_LATITUDE    if ($changes =~ m/\+LATITUDE\b/);
    $log_format |= $LOG_FORMAT_LONGITUDE   if ($changes =~ m/\+LONGITUDE\b/);
    $log_format |= $LOG_FORMAT_HEIGHT      if ($changes =~ m/\+HEIGHT\b/);
    $log_format |= $LOG_FORMAT_SPEED       if ($changes =~ m/\+SPEED\b/);
    $log_format |= $LOG_FORMAT_HEADING     if ($changes =~ m/\+HEADING\b/);
    $log_format |= $LOG_FORMAT_DSTA        if ($changes =~ m/\+DSTA\b/);
    $log_format |= $LOG_FORMAT_DAGE        if ($changes =~ m/\+DAGE\b/);
    $log_format |= $LOG_FORMAT_PDOP        if ($changes =~ m/\+PDOP\b/);
    $log_format |= $LOG_FORMAT_HDOP        if ($changes =~ m/\+HDOP\b/);
    $log_format |= $LOG_FORMAT_VDOP        if ($changes =~ m/\+VDOP\b/);
    $log_format |= $LOG_FORMAT_NSAT        if ($changes =~ m/\+NSAT\b/);
    $log_format |= $LOG_FORMAT_SID         if ($changes =~ m/\+SID\b/);
    $log_format |= $LOG_FORMAT_ELEVATION   if ($changes =~ m/\+ELEVATION\b/);
    $log_format |= $LOG_FORMAT_AZIMUTH     if ($changes =~ m/\+AZIMUTH\b/);
    $log_format |= $LOG_FORMAT_SNR         if ($changes =~ m/\+SNR\b/);
    $log_format |= $LOG_FORMAT_RCR         if ($changes =~ m/\+RCR\b/);
    $log_format |= $LOG_FORMAT_MILLISECOND if ($changes =~ m/\+MILLISECOND\b/);
    $log_format |= $LOG_FORMAT_DISTANCE    if ($changes =~ m/\+DISTANCE\b/);

    $log_format ^= $LOG_FORMAT_UTC         if ($changes =~ m/-UTC\b/);
    $log_format ^= $LOG_FORMAT_VALID       if ($changes =~ m/-VALID\b/);
    $log_format ^= $LOG_FORMAT_LATITUDE    if ($changes =~ m/-LATITUDE\b/);
    $log_format ^= $LOG_FORMAT_LONGITUDE   if ($changes =~ m/-LONGITUDE\b/);
    $log_format ^= $LOG_FORMAT_HEIGHT      if ($changes =~ m/-HEIGHT\b/);
    $log_format ^= $LOG_FORMAT_SPEED       if ($changes =~ m/-SPEED\b/);
    $log_format ^= $LOG_FORMAT_HEADING     if ($changes =~ m/-HEADING\b/);
    $log_format ^= $LOG_FORMAT_DSTA        if ($changes =~ m/-DSTA\b/);
    $log_format ^= $LOG_FORMAT_DAGE        if ($changes =~ m/-DAGE\b/);
    $log_format ^= $LOG_FORMAT_PDOP        if ($changes =~ m/-PDOP\b/);
    $log_format ^= $LOG_FORMAT_HDOP        if ($changes =~ m/-HDOP\b/);
    $log_format ^= $LOG_FORMAT_VDOP        if ($changes =~ m/-VDOP\b/);
    $log_format ^= $LOG_FORMAT_NSAT        if ($changes =~ m/-NSAT\b/);
    $log_format ^= $LOG_FORMAT_SID         if ($changes =~ m/-SID\b/);
    $log_format ^= $LOG_FORMAT_ELEVATION   if ($changes =~ m/-ELEVATION\b/);
    $log_format ^= $LOG_FORMAT_AZIMUTH     if ($changes =~ m/-AZIMUTH\b/);
    $log_format ^= $LOG_FORMAT_SNR         if ($changes =~ m/-SNR\b/);
    $log_format ^= $LOG_FORMAT_RCR         if ($changes =~ m/-RCR\b/);
    $log_format ^= $LOG_FORMAT_MILLISECOND if ($changes =~ m/-MILLISECOND\b/);
    $log_format ^= $LOG_FORMAT_DISTANCE    if ($changes =~ m/-DISTANCE\b/);

    return($log_format);
}

#-------------------------------------------------------------------------
# Given a log format value (bitmask), return the record size in bytes.
#-------------------------------------------------------------------------
sub sizeof_log_format {

    my $log_format = shift;
    my $size = '';

    $size += $SIZEOF_LONG     if ($log_format & $LOG_FORMAT_UTC);
    $size += $SIZEOF_WORD     if ($log_format & $LOG_FORMAT_VALID);
    $size += $SIZEOF_DOUBLE   if ($log_format & $LOG_FORMAT_LATITUDE);
    $size += $SIZEOF_DOUBLE   if ($log_format & $LOG_FORMAT_LONGITUDE);
    $size += $SIZEOF_FLOAT    if ($log_format & $LOG_FORMAT_HEIGHT);
    $size += $SIZEOF_FLOAT    if ($log_format & $LOG_FORMAT_SPEED);
    $size += $SIZEOF_FLOAT    if ($log_format & $LOG_FORMAT_HEADING);
    $size += $SIZEOF_WORD     if ($log_format & $LOG_FORMAT_DSTA);
    $size += $SIZEOF_LONG     if ($log_format & $LOG_FORMAT_DAGE);
    $size += $SIZEOF_WORD     if ($log_format & $LOG_FORMAT_PDOP);
    $size += $SIZEOF_WORD     if ($log_format & $LOG_FORMAT_HDOP);
    $size += $SIZEOF_WORD     if ($log_format & $LOG_FORMAT_VDOP);
    $size += $SIZEOF_BYTE * 2 if ($log_format & $LOG_FORMAT_NSAT);
    $size += $SIZEOF_BYTE     if ($log_format & $LOG_FORMAT_SID);
    $size += $SIZEOF_WORD     if ($log_format & $LOG_FORMAT_ELEVATION);
    $size += $SIZEOF_WORD     if ($log_format & $LOG_FORMAT_AZIMUTH);
    $size += $SIZEOF_WORD     if ($log_format & $LOG_FORMAT_SNR);
    $size += $SIZEOF_WORD     if ($log_format & $LOG_FORMAT_RCR);
    $size += $SIZEOF_WORD     if ($log_format & $LOG_FORMAT_MILLISECOND);
    $size += $SIZEOF_DOUBLE   if ($log_format & $LOG_FORMAT_DISTANCE);

    return($size);

}

#-------------------------------------------------------------------------
# Return a string describing the log record separator type.
#-------------------------------------------------------------------------
sub describe_separator_type {
    my $sep_type = shift;
    return('CHANGE_LOG_BITMASK')    if ($sep_type == $SEP_TYPE_CHANGE_LOG_BITMASK);
    return('CHANGE_LOG_PERIOD')     if ($sep_type == $SEP_TYPE_CHANGE_LOG_PERIOD);
    return('CHANGE_LOG_DISTANCE')   if ($sep_type == $SEP_TYPE_CHANGE_LOG_DISTANCE);
    return('CHANGE_LOG_SPEED')      if ($sep_type == $SEP_TYPE_CHANGE_LOG_SPEED);
    return('CHANGE_OVERWRITE_STOP') if ($sep_type == $SEP_TYPE_CHANGE_OVERWRITE_STOP);
    return('CHANGE_START_STOP_LOG') if ($sep_type == $SEP_TYPE_CHANGE_START_STOP_LOG);
    return('UNKNOWN');
}

#-------------------------------------------------------------------------
# Return a string describing some field.
#-------------------------------------------------------------------------
sub describe_valid_field {
    my $valid = shift;
    return('NO_FIX')       if ($valid == $VALID_NO_FIX);
    return('SPS')          if ($valid == $VALID_SPS);
    return('DGPS')         if ($valid == $VALID_DGPS);
    return('PPS')          if ($valid == $VALID_PPS);
    return('RTK')          if ($valid == $VALID_RTK);
    return('FRTK')         if ($valid == $VALID_FRTK);
    return('ESTIMATED')    if ($valid == $VALID_ESTIMATED);
    return('MANUAL_INPUT') if ($valid == $VALID_MANUAL);
    return('SIMULATOR')    if ($valid == $VALID_SIMULATOR);
    return('UNKNOWN');
}

# Description suitable for GPX <trkpt> <fix> element.
sub describe_valid_gpx {
    my $valid = shift;
    return('none')         if ($valid == $VALID_NO_FIX);
    return('3d')           if ($valid == $VALID_SPS);
    return('dgps')         if ($valid == $VALID_DGPS);
    return('pps')          if ($valid == $VALID_PPS);
    return(undef);
}

sub describe_rcr_field {
    my $rcr = shift;
    return('TIME')          if ($rcr == $RCR_TIME);
    return('SPEED')         if ($rcr == $RCR_SPEED);
    return('DISTANCE')      if ($rcr == $RCR_DISTANCE);
    return('INTEREST')      if ($rcr == $RCR_INTEREST);
    return('UNKNOWN');
}

# Description suitable for GPX <trkpt> <type> element.
sub describe_rcr_gpx {
    my $rcr = shift;
    return('TIME')          if ($rcr == $RCR_TIME);
    return('SPEED')         if ($rcr == $RCR_SPEED);
    return('DISTANCE')      if ($rcr == $RCR_DISTANCE);
    return('INTEREST')      if ($rcr == $RCR_INTEREST);
    return(undef);
}


sub describe_recording_status {
    my $val = shift;
    return('ON') if ($val & 0x022);
    return('OFF');
}

sub describe_recording_method {
    my $val = shift;
    return('OVERWRITE') if ($val == 1);
    return('STOP')      if ($val == 2);
    return('UNKNOWN');
}

#-------------------------------------------------------------------------
# Silly function: return a byte!
#-------------------------------------------------------------------------
sub mtk2byte {
    ord(shift);
}

#-------------------------------------------------------------------------
# Convert a 16 bit binary data into an integer.
#-------------------------------------------------------------------------
sub mtk2word {
    unpack('S', shift);
}

#-------------------------------------------------------------------------
# Convert a 32 bit binary data into a long integer number.
#-------------------------------------------------------------------------
sub mtk2long {
    unpack('L', shift);
}

#-------------------------------------------------------------------------
# Convert a 32 bit binary data into a float number.
#-------------------------------------------------------------------------
sub mtk2float {
    unpack('f', shift);
}

#-------------------------------------------------------------------------
# Convert a 64 bit binary data into a double precision number.
#-------------------------------------------------------------------------
sub mtk2double {
    unpack('d', shift);
}

#-------------------------------------------------------------------------
# Convert seconds from epoch (long int) to UTM timestamp. 
#-------------------------------------------------------------------------
sub mtk2time {
    time2str('%Y-%m-%dT%H:%M:%SZ', mtk2long(shift), 'GMT');
}

#-------------------------------------------------------------------------
# Convert seconds from epoch (long int) to UTM timestamp.
#-------------------------------------------------------------------------
sub utm_time {
    time2str('%Y-%m-%dT%H:%M:%SZ', shift, 'GMT');
}

#-------------------------------------------------------------------------
# Print the header of a GPX file.
#-------------------------------------------------------------------------
sub gpx_print_gpx_begin {
    my $fp     = shift;
    my $time   = shift;
    my $minlat = shift;
    my $minlon = shift;
    my $maxlat = shift;
    my $maxlon = shift;
    print $fp sprintf('<?xml version="1.0" encoding="UTF-8"?>%s', $GPX_EOL);
    print $fp '<gpx' . $GPX_EOL;
    print $fp '  version="1.1"' . $GPX_EOL;
    print $fp '  creator="MTKBabel - http://www.rigacci.org/"' . $GPX_EOL;
    print $fp '  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"' . $GPX_EOL;
    print $fp '  xmlns="http://www.topografix.com/GPX/1/1"' . $GPX_EOL;
    print $fp '  xsi:schemaLocation="http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd">' . $GPX_EOL;
    print $fp sprintf('<metadata>%s', $GPX_EOL);
    print $fp sprintf('  <time>%s</time>%s', utm_time($time), $GPX_EOL);
    print $fp sprintf('  <bounds minlat="%.9f" minlon="%.9f" maxlat="%.9f" maxlon="%.9f"/>%s', $minlat, $minlon, $maxlat, $maxlon, $GPX_EOL);
    print $fp sprintf('</metadata>%s', $GPX_EOL);
}
sub gpx_print_gpx_end {
    my $fp = shift;
    print $fp sprintf('</gpx>%s', $GPX_EOL);
}

#-------------------------------------------------------------------------
# Open and close the GPX <trk> tag.
#-------------------------------------------------------------------------
sub gpx_print_trk_begin {
    my $fp = shift;
    print $fp sprintf('<trk>%s', $GPX_EOL);
    print $fp sprintf('  <name>%s</name>%s', $record_utc, $GPX_EOL);
    print $fp sprintf('  <number>%u</number>%s', $gpx_trk_number, $GPX_EOL) if ($gpx_trk_number > 0);;
    print $fp sprintf('<trkseg>%s', $GPX_EOL);
    $gpx_trk_number++;
}
sub gpx_print_trk_end {
    my $fp = shift;
    print $fp sprintf('</trkseg>%s', $GPX_EOL);
    print $fp sprintf('</trk>%s', $GPX_EOL);
}

#-------------------------------------------------------------------------
# Print a GPX <trkpt>.
#-------------------------------------------------------------------------
sub gpx_print_trkpt {
    my $fp = shift;
    print $fp sprintf('<trkpt lat="%.9f" lon="%.9f">%s', $record_latitude, $record_longitude, $GPX_EOL);
    print $fp sprintf('  <ele>%.6f</ele>%s',                   $record_height,       $GPX_EOL) if (defined($record_height));
    print $fp sprintf('  <time>%s</time>%s',                   $record_utc,          $GPX_EOL) if (defined($record_utc));
    print $fp sprintf('  <type>%s</type>%s', &describe_rcr_gpx($record_rcr),         $GPX_EOL) if (&describe_rcr_gpx($record_rcr));
    print $fp sprintf('  <fix>%s</fix>%s', &describe_valid_gpx($record_valid),       $GPX_EOL) if (&describe_valid_gpx($record_valid));
    print $fp sprintf('  <sat>%u</sat>%s',                     $record_nsat_in_use,  $GPX_EOL) if (defined($record_nsat_in_use));
    print $fp sprintf('  <hdop>%.2f</hdop>%s',                 $record_hdop,         $GPX_EOL) if (defined($record_hdop));
    print $fp sprintf('  <vdop>%.2f</vdop>%s',                 $record_vdop,         $GPX_EOL) if (defined($record_vdop));
    print $fp sprintf('  <pdop>%.2f</pdop>%s',                 $record_pdop,         $GPX_EOL) if (defined($record_pdop));
    print $fp sprintf('  <ageofdgpsdata>%u</ageofdgpsdata>%s', $record_dage,         $GPX_EOL) if (defined($record_dage));
    print $fp sprintf('  <dgpsid>%u</dgpsid>%s',               $record_dsta,         $GPX_EOL) if (defined($record_dsta));
    print $fp sprintf('  <extensions>%s', $GPX_EOL)  if ($opt_x);
    print $fp sprintf('    <%sspeed>%.6f</%sspeed>%s',       $opt_x, $record_speed,        $opt_x, $GPX_EOL) if (defined($record_speed));
    print $fp sprintf('    <%sheading>%.6f</%sheading>%s',   $opt_x, $record_heading,      $opt_x, $GPX_EOL) if (defined($record_heading));
    print $fp sprintf('    <%snsat>%u/%u</%snsat>%s',        $opt_x, $record_nsat_in_use, $record_nsat_in_view, $opt_x, $GPX_EOL) if (defined($record_nsat_in_use));
    print $fp sprintf('    <%smsec>%03u</%smsec>%s',         $opt_x, $record_millisecond,  $opt_x, $GPX_EOL) if (defined($record_millisecond));
    print $fp sprintf('    <%sdistance>%.9f</%sdistance>%s', $opt_x, $record_distance,     $opt_x, $GPX_EOL) if (defined($record_distance));
    print $fp sprintf('  </extensions>%s', $GPX_EOL) if ($opt_x);
    print $fp sprintf('</trkpt>%s', $GPX_EOL);
    $gpx_trk_minlat = $record_latitude  if ($record_latitude  < $gpx_trk_minlat);
    $gpx_trk_maxlat = $record_latitude  if ($record_latitude  > $gpx_trk_maxlat);
    $gpx_trk_minlon = $record_longitude if ($record_longitude < $gpx_trk_minlon);
    $gpx_trk_maxlon = $record_longitude if ($record_longitude > $gpx_trk_maxlon);
}

#-------------------------------------------------------------------------
# Print a GPX <wpt>.
#-------------------------------------------------------------------------
sub gpx_print_wpt {
    my $fp = shift;
    $gpx_wpt_number++;
    print $fp sprintf('<wpt lat="%.9f" lon="%.9f">%s', $record_latitude, $record_longitude, $GPX_EOL);
    print $fp sprintf('  <ele>%.6f</ele>%s',                   $record_height,       $GPX_EOL) if (defined($record_height));
    print $fp sprintf('  <time>%s</time>%s',                   $record_utc,          $GPX_EOL) if (defined($record_utc));
    print $fp sprintf('  <fix>%s</fix>%s', &describe_valid_gpx($record_valid),       $GPX_EOL) if (&describe_valid_gpx($record_valid));
    print $fp sprintf('  <name>%03d</name>%s',                 $gpx_wpt_number,      $GPX_EOL);
    print $fp sprintf('  <cmt>%03d</cmt>%s',                   $gpx_wpt_number,      $GPX_EOL);
    print $fp sprintf('  <desc>%s</desc>%s',                   $record_utc,          $GPX_EOL) if (defined($record_utc));
    print $fp sprintf('  <sym>Flag</sym>%s', $GPX_EOL);
    print $fp sprintf('</wpt>%s', $GPX_EOL);
    $gpx_wpt_minlat = $record_latitude  if ($record_latitude  < $gpx_wpt_minlat);
    $gpx_wpt_maxlat = $record_latitude  if ($record_latitude  > $gpx_wpt_maxlat);
    $gpx_wpt_minlon = $record_longitude if ($record_longitude < $gpx_wpt_minlon);
    $gpx_wpt_maxlon = $record_longitude if ($record_longitude > $gpx_wpt_maxlon);
}

#-------------------------------------------------------------------------
# Log time.
#-------------------------------------------------------------------------
sub log_time {
    time2str('%H:%M:%S', time());
}
