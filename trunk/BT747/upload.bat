SETLOCAL
SET APP_LANG=en

SET APP_LANG_EXT=__L%APP_LANG%
if %APP_LANG%==en SET APP_LANG_EXT=

if NOT x%1==x goto %1:

goto normal:
goto end:

goto jnlp:
goto normal:
goto japfonts:
goto fonts:

:normal
REM Signing done in build.xml now - next lines commented
REM call sign dist/BT747.jar
REM call sign dist/BT747_rxtx.jar
scp dist/BT747.jar shell.sourceforge.net:bt747/htdocs/BT747%APP_LANG_EXT%.jar
ssh shell.sourceforge.net chmod 755 bt747/htdocs/BT747%APP_LANG_EXT%.jar
scp dist/BT747_rxtx.jar shell.sourceforge.net:bt747/htdocs/BT747_rxtx%APP_LANG_EXT%.jar
ssh shell.sourceforge.net chmod 755 bt747/htdocs/BT747_rxtx%APP_LANG_EXT%.jar
goto end:

:jnlplibs

ssh shell.sourceforge.net chmod 755 bt747/htdocs/swing-layout-1.0.3.jar
scp lib/swing-layout-1.0.3.jar shell.sourceforge.net:bt747/htdocs/swing-layout-1.0.3.jar
ssh shell.sourceforge.net chmod 755 bt747/htdocs/swing-layout-1.0.3.jar
goto end:

:forj2se

ssh shell.sourceforge.net chmod 755 bt747/htdocs/waba_forj2se.jar
scp lib/waba_forj2se.jar shell.sourceforge.net:bt747/htdocs/waba_forj2se.jar
ssh shell.sourceforge.net chmod 755 bt747/htdocs/waba_forj2se.jar
goto end:

:coll
ssh shell.sourceforge.net chmod 755 bt747/htdocs/collections-superwaba.jar
scp lib/collections-superwaba.jar shell.sourceforge.net:bt747/htdocs/collections-superwaba.jar
ssh shell.sourceforge.net chmod 755 bt747/htdocs/collections-superwaba.jar

goto end:

:j2sejnlp

ssh shell.sourceforge.net chmod 755 bt747/htdocs/BT747_J2SE.jnlp
scp BT747_web_J2SE.jnlp shell.sourceforge.net:bt747/htdocs/BT747_J2SE.jnlp
ssh shell.sourceforge.net chmod 755 bt747/htdocs/BT747_J2SE.jnlp

ssh shell.sourceforge.net chmod 755 bt747/htdocs/BT747_J2SE_only.jnlp
scp BT747_web_J2SE_only.jnlp shell.sourceforge.net:bt747/htdocs/BT747_J2SE_only.jnlp
ssh shell.sourceforge.net chmod 755 bt747/htdocs/BT747_J2SE_only.jnlp
goto end:

:j2se
ssh shell.sourceforge.net chmod 755 bt747/htdocs/BT747_j2se.jar
scp dist/BT747_j2se.jar shell.sourceforge.net:bt747/htdocs/BT747_j2se.jar
ssh shell.sourceforge.net chmod 755 bt747/htdocs/BT747_j2se.jar

goto end:

:jnlp
REM Next line to upload Java web start specification
ssh shell.sourceforge.net chmod 755 bt747/htdocs/BT747.jnlp
scp BT747_web.jnlp shell.sourceforge.net:bt747/htdocs/BT747.jnlp
ssh shell.sourceforge.net chmod 755 bt747/htdocs/BT747.jnlp
scp BT747_win32comm_web.jnlp shell.sourceforge.net:bt747/htdocs/BT747_win32comm.jnlp
goto end:

:fonts
scp lib/WabaKor.jar shell.sourceforge.net:bt747/htdocs/WabaKor.jar
ssh shell.sourceforge.net chmod 755 bt747/htdocs/WabaChi.jar
scp lib/WabaChi.jar shell.sourceforge.net:bt747/htdocs/WabaChi.jar
ssh shell.sourceforge.net chmod 755 bt747/htdocs/WabaKor.jar
:japfonts
scp lib/WabaJap.jar shell.sourceforge.net:bt747/htdocs/WabaJap.jar
ssh shell.sourceforge.net chmod 755 bt747/htdocs/WabaJap.jar
goto end:

:end

ENDLOCAL
