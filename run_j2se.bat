@echo off
setlocal
set       MYROOTPATH="%~dp0"
for /f "useback tokens=*" %%a in ('%MYROOTPATH%') do set MYROOTPATH=%%~a

set           MYDIST="%MYROOTPATH%dist"
for /f "useback tokens=*" %%a in ('%MYDIST%') do set MYDIST=%%~a

set            MYLIB="%MYROOTPATH%lib"
for /f "useback tokens=*" %%a in ('%MYLIB%') do set MYLIB=%%~a

set         RXTXPATH="%MYLIB%\rxtx-2.1-7-bins-r2"
for /f "useback tokens=*" %%a in ('%RXTXPATH%') do set RXTXPATH=%%~a

set MYSYSTEMRXTXBINS="%RXTXPATH%\Windows\i368-mingw32"
for /f "useback tokens=*" %%a in ('%MYSYSTEMRXTXBINS%') do set MYSYSTEMRXTXBINS=%%~a

set             PATH="%MYSYSTEMRXTXBINS%;%MYROOTPATH%;%JAVA_HOME%\bin;%PATH%"
for /f "useback tokens=*" %%a in ('%PATH%') do set PATH=%%~a

set        CLASSPATH="%MYDIST%\BT747_j2se.jar;%MYLIB%\swingx-ws.jar;%MYLIB%\swingx.jar;%RXTXPATH%\RXTXcomm.jar;%MYDIST%\libBT747.jar;%MYLIB%\swing-layout-1.0.3.jar;%MYLIB%\jopt-simple-2.4.1.jar;%MYLIB%\jchart2d-3.1.0.jar;%CLASSPATH%"
for /f "useback tokens=*" %%a in ('%CLASSPATH%') do set CLASSPATH=%%~a

REM java -Dbt747_prefix="COM" -Dbt747_settings="bt747settings.pdb" waba.applet.Applet BT747

rem echo PATH = %PATH%
rem echo CLASSPATH = %CLASSPATH%

SET  MEM_HEAP_OPTION=-Xmx192m

REM Uncommenting the next line will enable some logging by RXTX to file 'asdf'.
REM set  DEBUG_OPTION=-Dgnu.io.log.mode=FILE_MODE
REM set CLASSPATH=%RXTXPATH%\RXTXcomm-debug.jar;%CLASSPATH%

REM Change javaw to java in next line to see startup and debug messages
if x"%*"==x"debug" goto debug:
if NOT x"%*"==x"" goto cmdline:
echo %CLASSPATH%
echo java %MEM_HEAP_OPTION% %DEBUG_OPTION% bt747.j2se_view.BT747Main %*
START javaw %MEM_HEAP_OPTION% %DEBUG_OPTION% bt747.j2se_view.BT747Main %*
goto end:
:cmdline
java %MEM_HEAP_OPTION% %DEBUG_OPTION% bt747.j2se_view.BT747Main %*
goto end:
:debug
set DEBUG_OPTION=-Dgnu.io.log.mode=FILE_MODE

set CLASSPATH="%RXTXPATH%\RXTXcomm-debug.jar;%CLASSPATH%"
for /f "useback tokens=*" %%a in ('%CLASSPATH%') do set CLASSPATH=%%~a

java %MEM_HEAP_OPTION% %DEBUG_OPTION% -verbose bt747.j2se_view.BT747Main
pause
:end
pause
endlocal
