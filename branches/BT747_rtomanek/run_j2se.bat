@echo on
setlocal
set       MYROOTPATH=%~dp0
set           MYDIST=%MYROOTPATH%dist
set            MYLIB=%MYROOTPATH%lib
set         RXTXPATH=%MYLIB%\rxtx-2.1-7-bins-r2
set MYSYSTEMRXTXBINS=%RXTXPATH%\Windows\i368-mingw32
set             PATH=%MYSYSTEMRXTXBINS%;%MYROOTPATH%;%JAVA_HOME%\bin;%PATH%
set        CLASSPATH=%MYDIST%\BT747_j2se.jar;%MYLIB%\swingx-ws.jar;%MYLIB%\swingx.jar;%RXTXPATH%\RXTXcomm.jar;%MYDIST%\libBT747.jar;%MYLIB%\swing-layout-1.0.3.jar;%MYLIB%\jopt-simple-2.4.1.jar;%CLASSPATH%

REM java -Dbt747_prefix="COM" -Dbt747_settings="bt747settings.pdb" waba.applet.Applet BT747

rem echo PATH = %PATH%
rem echo CLASSPATH = %CLASSPATH%

SET  MEM_HEAP_OPTION=-Xmx192m

REM Change javaw to java in next line to see startup and debug messages
if x"%*"==x"debug" goto debug:
if NOT x"%*"==x"" goto cmdline:
START javaw %MEM_HEAP_OPTION% bt747.j2se_view.BT747Main %*
goto end:
:cmdline
java %MEM_HEAP_OPTION% bt747.j2se_view.BT747Main %*
goto end:
:debug
java %MEM_HEAP_OPTION% -verbose bt747.j2se_view.BT747Main
pause
:end
endlocal
