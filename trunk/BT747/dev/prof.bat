@echo off
set ORGPATH=%PATH%

REM Next line adds Java 1.4 installation path
REM set JAVA_HOME=c:\j2sdk1.4.2_14
set MYROOTPATH=.
set RXTXPATH=%MYROOTPATH%\rxtx-2.1-7-bins-r2
set MYSYSTEMRXTXBINS=%RXTXPATH%\Windows\i368-mingw32
set PATH=%MYSYSTEMRXTXBINS%;%JAVA_HOME%\bin;%PATH%
mkdir bt_unzip
cd bt_unzip
unzip ..\dist\BT747_rxtx.jar
cd ..
REM set CLASSPATH=%RXTXPATH%\RXTXcomm.jar;webstart\waba_only.jar;dist\BT747_rxtx.jar;.;%CLASSPATH%
set CLASSPATH=%RXTXPATH%\RXTXcomm.jar;webstart\waba_only.jar;bt_unzip;.;%CLASSPATH%

REM java -Dbt747_prefix="COM" -Dbt747_settings="bt747settings.pdb" waba.applet.Applet BT747
java -agentlib:hprof=cpu=samples,interval=5,lineno=y,file=java.hprof.txt waba.applet.Applet  /w 320 /h 320 /scale 1 /bpp 8 BT747


REM      HPROF: Heap and CPU Profiling Agent (JVMTI Demonstration Code)
REM 
REM hprof usage: java -agentlib:hprof=[help]|[<option>=<value>, ...]
REM 
REM Option Name and Value  Description                    Default
REM ---------------------  -----------                    -------
REM heap=dump|sites|all    heap profiling                 all
REM cpu=samples|times|old  CPU usage                      off
REM monitor=y|n            monitor contention             n
REM format=a|b             text(txt) or binary output     a
REM file=<file>            write data to file             java.hprof[{.txt}]
REM net=<host>:<port>      send data over a socket        off
REM depth=<size>           stack trace depth              4
REM interval=<ms>          sample interval in ms          10
REM cutoff=<value>         output cutoff point            0.0001
REM lineno=y|n             line number in traces?         y
REM thread=y|n             thread in traces?              n
REM doe=y|n                dump on exit?                  y
REM msa=y|n                Solaris micro state accounting n
REM force=y|n              force output to <file>         y
REM verbose=y|n            print messages about dumps     y
REM 
REM Obsolete Options
REM ----------------
REM gc_okay=y|n
REM 
REM Examples
REM --------
REM   - Get sample cpu information every 20 millisec, with a stack depth of 3:
REM       java -agentlib:hprof=cpu=samples,interval=20,depth=3 classname
REM   - Get heap usage information based on the allocation sites:
REM       java -agentlib:hprof=heap=sites classname
REM 
REM Notes
REM -----
REM   - The option format=b cannot be used with monitor=y.
REM   - The option format=b cannot be used with cpu=old|times.
REM   - Use of the -Xrunhprof interface can still be used, e.g.
REM        java -Xrunhprof:[help]|[<option>=<value>, ...]
REM     will behave exactly the same as:
REM        java -agentlib:hprof=[help]|[<option>=<value>, ...]
REM 
REM Warnings
REM --------
REM   - This is demonstration code for the JVMTI interface and use of BCI,
REM     it is not an official product or formal part of the JDK.
REM   - The -Xrunhprof interface will be removed in a future release.
REM   - The option format=b is considered experimental, this format may change
REM     in a future release.
set PATH=%ORGPATH%
