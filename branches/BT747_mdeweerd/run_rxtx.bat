@echo off
setlocal

REM Next line adds Java 1.4 installation path

set       MYROOTPATH=%~dp0%
set           MYDIST=%MYROOTPATH%\dist
set            MYLIB=%MYROOTPATH%\lib
set         RXTXPATH=%MYLIB%\rxtx-2.1-7-bins-r2
set MYSYSTEMRXTXBINS=%RXTXPATH%\Windows\i368-mingw32
set             PATH=%MYSYSTEMRXTXBINS%;%MYROOTPATH%;%JAVA_HOME%\bin;%PATH%
set        CLASSPATH=%MYDIST%\BT747_j2se.jar;%RXTXPATH%\RXTXcomm.jar;%MYDIST%\libBT747.jar;%MYLIB%\swing-layout-1.0.3.jar;%MYLIB%\jopt-simple-2.4.1.jar;%CLASSPATH%

set        CLASSPATH=%RXTXPATH%\RXTXcomm.jar;%MYLIB%\waba_only.jar;%MYDIST%\BT747_rxtx.jar;%CLASSPATH%

REM java -Dbt747_prefix="COM" -Dbt747_settings="bt747settings.pdb" waba.applet.Applet BT747

REM Change javaw to java in next line to see startup and debug messages
start javaw waba.applet.Applet  /w 320 /h 320 /scale 1 /bpp 8 BT747

endlocal
