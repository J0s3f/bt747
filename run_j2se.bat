@echo off
setlocal
set MYROOTPATH=%CD%
set RXTXPATH=%MYROOTPATH%\lib\rxtx-2.1-7-bins-r2
set MYSYSTEMRXTXBINS=%RXTXPATH%\Windows\i368-mingw32
set PATH=%MYSYSTEMRXTXBINS%;%CD%;%PATH%
REM REMOVED SYSTEM CLASSPATH FROM CLASSPATH ;%CLASSPATH%
set CLASSPATH=dist\BT747_j2se.jar;%RXTXPATH%\RXTXcomm.jar;dist\libBT747.jar;lib\swing-layout-1.0.3.jar

REM java -Dbt747_prefix="COM" -Dbt747_settings="bt747settings.pdb" waba.applet.Applet BT747

echo PATH = %PATH%
echo CLASSPATH = %CLASSPATH%

REM Change javaw to java in next line to see startup and debug messages
java -Dbt747_settings="bt747_DirkSettings.pdb" bt747.j2se_view.BT747Main
REM java -verbose bt747.j2se_view.BT747Main | more
pause
endlocal
