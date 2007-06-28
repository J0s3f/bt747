BT747 on SuperWaba

This project is under development.

It is hosted in http://sourceforge.net/projects/bt747.  That is where you can contact the author, ... .

You can find binaries in the 'dist' directory:
  - Palm  (BT747.prc, BT747.pdb)
  - WinCE (Untested: BT747_Install.BAT)
  - BT747.jar

You can find sources in the 'src' directory.

Symbian does not support Bluetooth, so the binaries are not included.
The "BT747.jar" might work on Mac, if the "rxtx" module is properly installed (can't try)


You will need to install/unzip SuperWaba on the platform you want to run BT747 on.

You can find SuperWaba installation files at http://www.superwaba.org/install

To run the BT747.jar, you need to install the "win32comm" driver or "rxtx" and use java 1.4.X.

On windows, I launch the BT747.jar like this:

-------------- start of example ----------------
set PATH=c:\j2sdk1.4.2_14\bin;%PATH%
set JAVA_HOME=c:\j2sdk1.4.2_14
set CLASSPATH=c:\pathtowin32comm\win32comm.jar;.;%CLASSPATH%;;c:\superwabasdk\lib\superwaba.jar
java waba.applet.Applet BT747
---------------- end of example ----------------

You can also try to run the 'run_ex.bat' command script.

