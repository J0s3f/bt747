**************************************************************************

 ######  #######  #####      #    #####
  #    # #  #  #  #   #     ##    #   #
  #    #    #         #     ##        #
  #    #    #        #     # #       #
  #####     #        #     # #       #
  #    #    #       #     #  #      #
  #    #    #       #     #####     #
  #    #    #      #         #     #
 ######    ###     #        ###    #
                                        
**************************************************************************
BT747 is a Java program to have control over most MTK Based GPS devices.
It should also be able to download and convert logs if the GPS device is a
logger.
**************************************************************************
BT747 is open source, free SW.  I coded about 40000 lines for the PDA and
Desktop version and an extra 7000 for the Mobile Phone version.
With the accessory files, that is around 50000 lines.
It runs on many systems which might seem logical because it is 'Java', but
it is not!  The Java Platforms are: J2SE, J2ME and SuperWaba.
The Systems are: Palm, WinCE (PocketPC), Java Phones, Windows, MacOSX, Linux.
**************************************************************************
You can also have a look at http://www.bt747.org
**************************************************************************

**************************************************************************
INSTALLATION
**************************************************************************
To run BT747 on your desktop system, the following generic steps are required.

1. Install the Serial Port driver or setup the Bluetooth port.
2. On MacOSX, Linux: change some access rights in the /var/lock directory.
3. Launch the appropriate script for your system and for the program version:

   Desktop Like Version:
    - Windows: run_j2se.bat
    - MacOSX : bt747_macosX_j2se.command
    - Linux  : run_j2se.sh
    
   PDA Like Version:
    - Windows: run_rxtx.bat
    - MacOSX : bt747_maxosX.command
    - Linux  : run_rxtx.sh

 Step 3 can also be done directly from the web: http://www.bt747.org

If step 1 and step 2 are done, this should run out of the box.  If it does not,
please contact the author.  The author has Windows XP, Palm and a Java
Mobile Phone - so to debug the other systems, your help is really needed in
case of trouble.
    
**************************************************************************
To install BT747 on your PDA, please see further below.
**************************************************************************
To install BT747 on your Java Phone, download the BT_J2ME version.
**************************************************************************
If you have suggestions to improve this README, leave a message on the forum or
a tracker at sourceforge: http://sf.net/projects/bt747,
or http://www.bt747.org .


Here is a small list of devices BT747 is known to work with:
- i-Blue 747
- i-Blue 757
- Qstarz BT-Q1000
- i.Trek Z1
- Holux GR-241;
- Holux M-241.


Further in this readme are instructions for installing on:
- Windows (reported working)
- PocketPC (reported working)
- Linux   (reported working)
- Palm    (reported working)
- Mac OS  (reported working)
- Debian  (over bluetooth, reported working)


========= PDA INSTRUCTIONS =================
========= PDA INSTRUCTIONS =================
========= PDA INSTRUCTIONS =================
========= PDA INSTRUCTIONS =================

Binaries are inside the 'dist' directory'
  - Palm  - Install BT747.prc, BT747.pdb  as usual.
  - WinCE - select the appropriate CAB file and install on your PDA.

You can find sources in the 'src' directory.

Symbian does not support Bluetooth, so the binaries are not included.

You will need to install/unzip SuperWaba on the platform you want to run BT747 on.

You can find SuperWaba installation files at http://www.superwaba.org/install

=========== Mac installation ==================
1.
You can get the drivers from SiLabs:
  http://www.silabs.com
Here is a direct link to the 'CP210x USB to UART Bridge Virtual COM Port (VCP)'
drivers :
  https://www.silabs.com/products/mcu/Pages/USBtoUARTBridgeVCPDrivers.aspx

2.
Then, you need to create a lock-file directory, and make it world-writable.

Open a shell, and type: 
  sudo mkdir /var/lock 
  sudo chmod 777 /var/lock 
 
Taken from: https://sourceforge.net/forum/message.php?msg_id=4571837

3.
Launch
   bt747_macosX_j2se.command
or
   bt747_maxosX.command
     
Ay default, the program should connect to the USB port, but you can have it connect
to the bluetooth port if you set the port to '/dev/tty.iBT-GPS-SPPslave-1' if you
specify the following parameter to Java:

-Dbt747_port="/dev/tty.iBT-GPS-SPPslave-1"

=========== PocketPC / PPC installation ================

The following 'script' indicates what was done to make it work in the PPC emulator:

I installed the PPC emulator on my PC.
I localized the time setting (Paris).
I defined the shared folder to some directory on my PC.
I copied the superwaba cab file and the BT747 cab file to this directory.
I installed Superwaba on the emulator (from the 'storage card')
I installed BT747 on the emulator ('from the storage card').
I mapped serial port 0 to COM4 (where the BT747 is on).
I launched BT747 (application).
I was able to connect to the device in the PPC emulator by clicking on '1'.
The application jumped to the log tab (as expected).
It showed the memory used and nbr of records used in this tab.
I set the 'Output dir' in the 'File' tab to '/Storage Card' (pointing to the shared directory actually).
I clicked 'Get log' on the 'Log' tab.
I cancelled 'Get log' (because the memory on my BT747 is almost full so it takes a while).
I clicked 'To GPX'. ... Conversion takes a while.
I set today's date as start date (I logged some GPS points today). Much faster (less to write).
I open the kml on windows -> opens google earth with expected points.


============= Linux ============================
1.
You can get the drivers from SiLabs - they are usually already 'installed':
  http://www.silabs.com
Here is a direct link to the 'CP210x USB to UART Bridge Virtual COM Port (VCP)'
drivers :
  https://www.silabs.com/products/mcu/Pages/USBtoUARTBridgeVCPDrivers.aspx

Use 'run_rxtx.sh' or 'run_j2se.sh'.

On 64 bit systems (i.e., java is 64 bit), you need to point to another driver.
Currently the script must be updated:
   RXTXLIBPATH=${RXTXPATH}/Linux/x86_64-unknown-linux-gnu

I hope to automate this in some future by checking `arch`

============= Debian over bluetooth ============
I struggled a while with gnu.io.NoSuchPortException when opening the port, but finally got it to run. 
 
Connecting and binding the gps unit was done like this: 
hcitool cc 00:xx:xx:xx:xx:xx 
rfcomm bind /dev/rfcomm0 00:xx:xx:xx:xx:xx 
 
 
My run_rxtx.sh looks like this: 
----------------------- 
MYROOTPATH=. 
 
SERPORT=/dev/rfcomm0 
 
RXTXLIBPATH=${MYROOTPATH}/rxtx-2.1-7-bins-r2/Linux/i686-unknown-linux-gnu 
export CLASSPATH=lib/Waba_only.jar:dist/BT747_rxtx.jar:rxtx-2.1-7-bins-r2/RXTXcomm.jar 
 
java -Dgnu.io.rxtx.SerialPorts=${SERPORT} -Dbt747_port=${SERPORT} -Djava.library.path=${RXTXLIBPATH} waba.applet.Applet /w 320 /h 320 /scale 1 /bpp 8 BT747 
----------------------- 
 
I had to add the '-Dgnu.io.rxtx.SerialPorts=${SERPORT}' to get rid of the NoSuchPortException, but now it works. 

=========================================================
ADVANCED INSTRUCTIONS

========= General (for pure rxtx/all systems) ===========
 Change the port prefix by adding the following option to the java invocation:
     (the example is for ports like /dev/ttyUSB0)

  -Dbt747_prefix="/dev/ttyUSB" 

java -Dbt747_prefix="/dev/ttyUSB" waba.applet.Applet BT747
  The port can be overridden entirely with the next option:

 -Dbt747_port="/dev/ttyUSB0"

  Another (or extra way) of forcing the port is the next option,
  one use apparently needed to do so to force the bluetooth port:
  -Dgnu.io.rxtx.SerialPorts=/dev/rfcomm0


It is also possible to set the path to the saved settings file (for the RXTX version):
  -Dbt747_settings="/another/yourpath"
=========================================


============ Contributors ======================
============ Contributors ======================
============ Contributors ======================
============ Contributors ======================
============ Contributors ======================
============ Contributors ======================
============ Contributors ======================
Mario De Weerd (main coder)
Dirk Haase (provided lots of feedback regarding the application and
            documentation for the German community.
            http://www.haased.de/gps_ge/bt747_start.html )
Herbert Geus (save settings on WinCE, KML waypoints addition, feedback on application)
Richard Akerman (information regarding connection on MacOSX to a Holux M-241 device)

============ Other Information =================
============ Other Information =================
============ Other Information =================
============ Other Information =================
============ Other Information =================
============ Other Information =================
============ Other Information =================

============ GMAPS PolylineEncoder =============
Taken from http://facstaff.unca.edu/mcmcclur/GoogleMaps/EncodePolyline/ and
adapted for SuperWaba:
/**
 * Reimplementation of Mark McClures Javascript PolylineEncoder
 * All the mathematical logic is more or less copied by McClure
 *  
 * @author Mark Rambow
 * @e-mail markrambow[at]gmail[dot]com
 * @version 0.1
 * 
 */


========== Japanese Fonts ======================
Used ufolib (http://jdict.sourceforge.net/ufolib/) to convert Japanese ttf fonts (http://sourceforge.jp/projects/efont)
to pdb format.

Created script to select characters needed:

============= script start =============
PROF=./myJap.profile
echo '<?xml version="1.0"?><UTFPROFILE><TITLECOMMENT VALUE="JapForBT747"/><RANGES>' > $PROF
echo '<RANGE START="9" END="10"/>' >> $PROF
echo '<RANGE START="13" END="13"/>' >> $PROF
echo '<RANGE START="32" END="126"/>' >> $PROF
echo '<RANGE START="160" END="255"/>' >> $PROF
perl -n -e 'while( /\\u(....)/gc ) { print "<RANGE START=\"".hex($1)."\" END=\"".hex($1)."\"/>\n" }' ../BT747/src/bt747/Txt_jp.java | uniq >> $PROF
echo '</RANGES></UTFPROFILE>' >> $PROF

CMD=./myJap.cmd
rm $CMD
echo 'open_font_file sazanami-20040629/sazanami-gothic.ttf' >> $CMD
#echo 'open_font_file sazanami-20040629/sazanami-mincho.ttf' >> $CMD
echo 'open_profile_file '$PROF >> $CMD
echo 'set_uff_family Jap' >> $CMD
echo 'save_uff_file' >> $CMD
echo 'exit' >> $CMD

java -cp './xerces/xerces.jar;./xerces/xercesSamples.jar;./CUP;./gnugetopt/java-getopt-1.0.9.jar;superwaba/SuperWaba.jar;ufolib.jar' ufolib.fontizer.Controller -c < $CMD

============ script end ==================

But needed to do the conversion manually in order to specify 'Ascend.' to 14 instead of the default '10' (graphical interface of Fontizer, UFF Font).
Loded the 'sazanami-gothic.ttf' font and the created 'myJap.profile' and moved the generated 'pdb' file to 'UFFJap_H.pdb'.

=========================================================
