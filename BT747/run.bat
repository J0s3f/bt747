
set ORGPATH=%PATH%
rem set PATH=c:\j2sdk1.4.2_14\bin;%PATH%
set PATH=c:\Program Files\java\jre1.6.0_01;%PATH%
set JAVA_HOME=c:\Program Files\java\jre1.6.0_0
rem set JAVA_HOME=c:\j2sdk1.4.2_14
set CLASSPATH=.;%CLASSPATH%;c:\superwabasdk\lib\superwaba.jar

start java waba.applet.Applet
start java waba.applet.Applet BT747
set PATH=%ORGPATH%
#debugn.bat  waba.applet.Applet BT747
rem c:\j2sdk1.4.2_14\bin\java.exe -classpath .;/projects/bt747;c:\superwabasdk\lib\superwaba.jar waba.applet.Applet BT747

