
set ORGPATH=%PATH%

set MYROOTPATH=.
set PATH=%JAVA_HOME%\bin;%PATH%
set CLASSPATH=lib\waba_only.jar;dist\BT747_file.jar;.;%CLASSPATH%

REM java -Dbt747_prefix="COM" -Dbt747_settings="bt747settings.pdb" waba.applet.Applet BT747
java waba.applet.Applet  /w 320 /h 320 /scale 1 /bpp 8 BT747
set PATH=%ORGPATH%
