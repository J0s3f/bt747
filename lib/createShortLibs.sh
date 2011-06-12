#/bin/sh
# This script was used to create a unified nativelib jar.
rm -rf shlib
mkdir shlib
cp ./rxtx-2.1-7-bins-r2/Linux/arm-xscale-linux-gnu/librxtxSerial.so shlib/libr217linarmser.so
cp ./rxtx-2.1-7-bins-r2/Linux/i686-unknown-linux-gnu/librxtxSerial.so shlib/libr217lini686ser.so
cp ./rxtx-2.1-7-bins-r2/Linux/ia64-unknown-linux-gnu/librxtxSerial.so shlib/libr217linia64ser.so
cp ./rxtx-2.1-7-bins-r2/Linux/x86_64-unknown-linux-gnu/librxtxSerial.so shlib/libr217linx86_64ser.so
cp ./rxtx-2.1-7-bins-r2/Mac_OS_X/librxtxSerial.jnilib shlib/libr217macser.jnilib
cp ./rxtx-2.1-7-bins-r2/Solaris/sparc-solaris/sparc32-sun-solaris2.8/librxtxSerial.so shlib/libr217sunos32ser.so
cp ./rxtx-2.1-7-bins-r2/Solaris/sparc-solaris/sparc64-sun-solaris2.8/librxtxSerial.so shlib/libr217sunos64ser.so
cp ./rxtx-2.1-7-bins-r2/Windows/i368-mingw32/rxtxSerial.dll shlib/r217winm32ser.dll
cp ./rxtx-2.2pre2-bins/Linux/i686-pc-linux-gnu/librxtxSerial.so shlib/libr22p2lini686ser.so
cp ./rxtx-2.2pre2-bins/Linux/x86_64-unknown-linux-gnu/librxtxSerial.so shlib/libr22p2linx86_64ser.so
cp ./rxtx-2.2pre2-bins/Mac_OS_X/mac-10.5/librxtxSerial.jnilib shlib/libr22p2mac10.5ser.jnilib
cp ./rxtx-2.2pre2-bins/Windows/win32/rxtxSerial.dll shlib/r22p2win32ser.dll
cp ./rxtx-2.2pre2-bins/Windows/win64/rxtxSerial.dll shlib/r22p2win64ser.dll
( cd shlib ; zip -9 ../rxtxnativelibs.zip * )
mv rxtxnativelibs.zip rxtxnativelibs.jar
( cd ../.. ; ./sign.bat BT747/lib/rxtxnativelibs.jar )
rm -rf shlib
