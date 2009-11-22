#!/bin/sh
mkdir tmp
cd tmp

SWT_BASE_DIR="/cygdrive/c/eclipse/plugins/org.eclipse.swt.win32*"
SWT_JAR=$SWT_BASE_DIR/ws/win32/swt.jar
SWT_DLL=$SWT_BASE_DIR/os/win32/x86/swt-win32-*.dll

jar -xvf $SWT_JAR

SWT_CLASSES=`find . -name "*.class" -print |sed 's/^\.\///'`

for i in $SWT_CLASSES
do
    OBJ_FILE=`echo $i |sed 's/\//_/g' |sed 's/\.class$/\.o/'`
    echo Compiling $i to $OBJ_FILE
    gcj -fjni -g0 -c -o $OBJ_FILE $i
done

gcj -c --resource=org.eclipse.swt.internal.SWTMessages \
    -o SWTMessages.o org/eclipse/swt/internal/SWTMessages.properties

echo Creating libswt.a
ar -rcs libswt.a *.o
ranlib libswt.a

echo Cleaning up
rm -fr org
rm -fr META-INF
rm -f version.txt
rm -f *.o

echo Copying SWT DLL
cp $SWT_DLL .

echo Copying SWT JAR
cp $SWT_JAR .

echo Done.

