This project is under development.

Due to similarity this SW written with a iBlue 747 device should work with the following
devices:
- i-Blue 747
- i-Bleu 757
- Qstarz BT-Q1000
- i.Trek Z1

It can also be used to configure a number of devices with the MTK chipset.
The log capabilities of the SW will then be useless for you.

Further in this readme are instructions for installing on:
- Windows
- PPC
- Linux
- Palm

========= General (for pure rxtx/all systems) ===========
# Change the port prefix by adding the following option to the java invocation:
#     (the example is for ports like /dev/ttyUSB0)
#  -Dbt747_prefix="/dev/ttyUSB" 
java -Dbt747_prefix="/dev/ttyUSB" waba.applet.Applet BT747
=========================================

========= WINDOWS INSTRUCTIONS =================
There are two different methods to make it work on windows:
a. Using RXTX directly
b. Using WIN32COMM

============== Windows: Using RXTX directly ===================
You need the full package or install rxtx yourself.

The 'RUN_RXTX.bat' file should work (or need minor adaptations).
===============================================================

============== Windows: Using WIN32COMM ===================
To run on 'windows', you should install WIN32COMM for java to get things going rapidly.
You need to install Java 1.4.X too.

Find some instructions (for another java version) here:
http://www.cs.uml.edu/~fredm/courses/91.305-fall04/javasetup.shtml

Basically, download this zip:
http://www.cs.uml.edu/~fredm/courses/91.305/software/JDK118-javaxcomm.zip

And install the files under java1.4.X insteady of the proposed java version.

Sun still has the files here:
http://javashoplm.sun.com/ECom/docs/Welcome.jsp?StoreId=22&PartDetailId=7235-javacomm-2.0-spec-oth-JSpec&SiteId=JSC&TransactionId=noreg 
=========================================================



It is hosted in http://sourceforge.net/projects/bt747.  That is where you can contact the author, ... .

You can find binaries in the 'dist' directory:
  - Palm  (BT747.prc, BT747.pdb)
  - WinCE (Untested: BT747_Install.BAT)
  - BT747.jar       (version using win32comm)
  - BT747_rxtx.jar  (version using rxtx)
  

You can find sources in the 'src' directory.

Symbian does not support Bluetooth, so the binaries are not included.
The "BT747.jar" might work on Mac, if the "rxtx" module is properly installed (http://www.rxtx.org).

On Linux, also install 'rxtx'.

You will need to install/unzip SuperWaba on the platform you want to run BT747 on.

You can find SuperWaba installation files at http://www.superwaba.org/install

=============== WINDOWS (again) =====================
To run the BT747.jar, you need to install the "win32comm" driver or "rxtx" and use java 1.4.X.

On windows, I launch the BT747.jar like this:

-------------- start of example ----------------
set PATH=c:\j2sdk1.4.2_14\bin;%PATH%
set JAVA_HOME=c:\j2sdk1.4.2_14
set CLASSPATH=c:\pathtowin32comm\win32comm.jar;c:\pathtowin32comm\build\BT747.jar;.;%CLASSPATH%;;c:\superwabasdk\lib\superwaba.jar
java waba.applet.Applet BT747
---------------- end of example ----------------

You can also try to run the 'run_ex.bat' command script.

=========== Palm installation ================
1. Install superwaba (http://www.superwaba.org/install
2. Install the BT747.prc and BT747.pdb file on your device.


=========== PPC installation ================

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
A specific JAR file has been created that should work specifically with RXTX (BT747_rxtx.jar).
Use 'run_rxtx.sh'.  It has not been validated on Linux, but it might work and at least provides the template.

============ Contributors ======================
Mario De Weerd (main coder)
Herbert Geus (save settings on WinCE, KML waypoints addition, feedback on application)