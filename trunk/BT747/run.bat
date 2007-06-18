
set ORGPATH=%PATH%
set PATH=c:\j2sdk1.4.2_14\bin;%PATH%
rem set PATH=c:\Program Files\java\jre1.6.0_01;%PATH%
rem set JAVA_HOME=c:\Program Files\java\jre1.6.0_0
set JAVA_HOME=c:\j2sdk1.4.2_14
set CLASSPATH=c:\projects\bt747\win32comm.jar;.;%CLASSPATH%;;c:\superwabasdk\lib\superwaba.jar

rem start java waba.applet.Applet
cd build
rem debugn.bat  waba.applet.Applet BT747
java -showversion
rem java waba.applet.Applet BT747
..\debugn.bat  waba.applet.Applet BT747
set PATH=%ORGPATH%
rem c:\j2sdk1.4.2_14\bin\java.exe -classpath .;c:\mdeweerd\/bt747;c:\superwabasdk\lib\superwaba.jar waba.applet.Applet BT747

