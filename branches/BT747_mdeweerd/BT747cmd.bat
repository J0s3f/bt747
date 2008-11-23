@echo off
setlocal
set       MYROOTPATH=%~dp0%
set           MYDIST=%MYROOTPATH%\dist
set            MYLIB=%MYROOTPATH%\lib
set         RXTXPATH=%MYLIB%\rxtx-2.1-7-bins-r2
set MYSYSTEMRXTXBINS=%RXTXPATH%\Windows\i368-mingw32
set             PATH=%MYSYSTEMRXTXBINS%;%MYROOTPATH%;%JAVA_HOME%\bin;%PATH%
set        CLASSPATH=%MYDIST%\BT747_j2se.jar;%RXTXPATH%\RXTXcomm.jar;%MYDIST%\libBT747.jar;%MYLIB%\swing-layout-1.0.3.jar;%MYLIB%\jopt-simple-2.4.1.jar;%CLASSPATH%

REM java -Dbt747_prefix="COM" -Dbt747_settings="bt747settings.pdb" waba.applet.Applet BT747

echo *********** Currently in BETA **************
java bt747.j2se_view.BT747cmd %*
echo *********** Currently in BETA **************
REM java -verbose bt747.j2se_view.BT747Main | more
endlocal
