@echo off
setlocal
set MYROOTPATH=%CD%
set RXTXPATH=%MYROOTPATH%\lib\rxtx-2.1-7-bins-r2
set MYSYSTEMRXTXBINS=%RXTXPATH%\Windows\i368-mingw32
set PATH=%MYSYSTEMRXTXBINS%;%CD%;%JAVA_HOME%\bin;%PATH%
set CLASSPATH=dist\BT747_j2se.jar;%RXTXPATH%\RXTXcomm.jar;dist\libBT747.jar;lib\swing-layout-1.0.3.jar;lib\jopt-simple-2.4.1.jar;%CLASSPATH%

REM java -Dbt747_prefix="COM" -Dbt747_settings="bt747settings.pdb" waba.applet.Applet BT747

REM Change javaw to java in next line to see startup and debug messages
echo *********** Currently in BETA **************
java bt747.j2se_view.BT747cmd %*
echo *********** Currently in BETA **************
REM java -verbose bt747.j2se_view.BT747Main | more
endlocal
