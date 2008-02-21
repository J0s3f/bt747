@echo off
setlocal
REM Next line adds Java 1.4 installation path
set JAVA_HOME=c:\j2sdk1.4.2_14
set MYROOTPATH=%CD%
set PATH=%JAVA_HOME%\bin;%PATH%
set CLASSPATH=win32comm.jar;%CD%\webstart\waba_only.jar;%CD%\dist\BT747.jar;%CLASSPATH%

REM change javaw to java in next line to see startup and debug messages
START javaw waba.applet.Applet /w 320 /h 320 /scale 1 /bpp 8 BT747
set PATH=%ORGPATH%
REM pause

endlocal
