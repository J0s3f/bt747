
set ORGPATH=%PATH%

REM Next line adds Java 1.4 installation path
REM set JAVA_HOME=c:\j2sdk1.4.2_14
set MYROOTPATH=.
set PATH=%JAVA_HOME%\bin;%PATH%
set CLASSPATH=webstart\waba_only.jar;dist\BT747_file.jar;.;%CLASSPATH%

REM java -Dbt747_prefix="COM" -Dbt747_settings="bt747settings.pdb" waba.applet.Applet BT747
java waba.applet.Applet  /w 320 /h 320 /scale 1 /bpp 8 BT747
set PATH=%ORGPATH%
