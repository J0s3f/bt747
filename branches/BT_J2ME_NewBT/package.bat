SETLOCAL
perl -n -e 'if(/MIDlet-Version: (.*)/) { $_=$1; s/\./_/g; print; }' deployed/DefaultCldcPhone2/BT_J2ME.jad > tmp

SET /P DT=<tmp
rm tmp
SET APP_LANG=en
SET PACK_DIR=pack
REM dos2unix *.sh
REM dos2unix *.command
REM unix2dos *.bat

rm -rf dist
mkdir dist
svn commit -m "Commit before packaging"
svn commit -m "Commit before packaging" ../BT747
svnversion -n -c > dist/SVNVERSION.txt
echo '' >> dist/SVNVERSION.txt
svnversion ../BT747 -n -c >> dist/SVNVERSION.txt

SET FILES=

SET FILES=%FILES% deployed/DefaultCldcPhone2/BT_J2ME.jar deployed/DefaultCldcPhone2/BT_J2ME.jad
SET FILES=%FILES% deployed/BT_J2ME.cod
SET FILES=%FILES% src_j4me/NOTICE doc/INSTALL.txt
SET FILES=%FILES% COPYING LICENSE-2.0-APACHE.txt

SET EXCLUDEFILES=DOESNOTEXIST

del %PACK_DIR%\BT_J2ME_%DT%.zip
mkdir %PACK_DIR%
cp %FILES% dist
call build_cod.bat
zip -9 -r %PACK_DIR%/BT_J2ME_%DT%.zip dist -x nbproject/private/\* \*/.svn/\* src/CVS/\* \*/CVS/\* nbproject/private %EXCLUDEFILES%
REM bash -c "../myrsync.sh %PACK_DIR%/BT_J2ME_%DT%.zip"
bash -c "../myrsync.sh %PACK_DIR%/BT_J2ME_%DT%.zip /home/frs/project/b/bt/bt747/bt747_dev/development"


CALL ..\uploadBT747.bat dist\BT_J2ME.jar J2ME/BT_J2ME.jar
CALL ..\uploadBT747.bat dist\BT_J2ME.jad J2ME/BT_J2ME.jad

ENDLOCAL