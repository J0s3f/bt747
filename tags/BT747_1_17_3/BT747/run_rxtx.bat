
set ORGPATH=%PATH%

REM Next line adds Java 1.4 installation path
REM set JAVA_HOME=c:\j2sdk1.4.2_14
set MYROOTPATH=.
set RXTXPATH=%MYROOTPATH%\rxtx-2.1-7-bins-r2
set MYSYSTEMRXTXBINS=%RXTXPATH%\Windows\i368-mingw32
set PATH=%MYSYSTEMRXTXBINS%;%JAVA_HOME%\bin;%PATH%
set CLASSPATH=%RXTXPATH%\RXTXcomm.jar;webstart\waba_only.jar;dist\BT747_rxtx.jar;.;%CLASSPATH%

REM java -Dbt747_prefix="COM" -Dbt747_settings="bt747settings.pdb" waba.applet.Applet BT747
start java waba.applet.Applet  /w 320 /h 320 /scale 1 /bpp 8 BT747
set PATH=%ORGPATH%
