
set ORGPATH=%PATH%

REM Next line adds Java 1.4 installation path
set JAVA_HOME=c:\j2sdk1.4.2_14
set PATH=%JAVA_HOME%\bin;%PATH%
set CLASSPATH=..\win32comm.jar;..\webstart\waba_only.jar;.;%CLASSPATH%

cd build
#gcj --boot waba.applet.Applet BT747.jar
gcj  BT747.jar
set PATH=%ORGPATH%
pause
