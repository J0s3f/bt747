
set ORGPATH=%PATH%

REM Next line adds Java 1.4 installation path
set JAVA_HOME=c:\j2sdk1.4.2_14
set JAVA_HOME="C:\Program Files\Java\jdk1.5.0_11"
set PATH=%JAVA_HOME%\bin;%PATH%
set CLASSPATH=..\lib\rxtx-2.1-7-bins-r2\RXTXcomm.jar;..\dist\libBT747.jar;..\lib\jopt-simple-2.4.1.jar;%CLASSPATH%
set CLASSPATH=..\lib\rxtx-2.1-7-bins-r2\RXTXcomm.jar;..\dist\BT747_j2SE.jar;..\dist\libBT747.jar;..\lib\jopt-simple-2.4.1.jar;%CLASSPATH%

cd build
REM gcj --boot waba.applet.Applet BT747.jar
REM gcj  --boot bt747.j2se_view.BT747cmd ..\dist\libBT747.jar
REM gcj  --main=bt747.j2se_view.BT747cmd ..\dist\libBT747.jar
javac -d . ..\src_j2se\bt747\j2se_view\BT747cmd.java
"C:\Program Files\Jarc\Mingw\bin\gcj.exe" --main=bt747.j2se_view.BT747cmd ..\dist\libBT747.jar ..\lib\rxtx-2.1-7-bins-r2\RXTXcomm.jar ..\lib\swingx-ws.jar .\bt747\j2se_view\BT747cmd.class ..\lib\jopt-simple-2.4.1.jar ..\lib\rxtx-2.1-7-bins-r2\Windows\i368-mingw32\rxtxSerial.dll
set PATH=%ORGPATH%
pause
