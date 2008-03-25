@echo off
setlocal
set MYROOTPATH=%CD%

set PATH=%MYROOTPATH%\lib;%PATH%
set CLASSPATH=%MYROOTPATH%\lib\win32comm.jar;%MYROOTPATH%\lib\waba_only.jar;%MYROOTPATH%\dist\BT747.jar;%CLASSPATH%

REM change javaw to java in next line to see startup and debug messages
START javaw waba.applet.Applet /w 320 /h 320 /scale 1 /bpp 8 BT747

endlocal
