SETLOCAL
SET APP_LANG=en

SET APP_LANG_EXT=__L%APP_LANG%
if %APP_LANG%==en SET APP_LANG_EXT=

goto jnlp:
goto normal:
goto japfonts:
goto fonts:

:normal
call sign dist/BT747.jar
call sign dist/BT747_rxtx.jar
scp dist/BT747.jar shell.sourceforge.net:bt747/htdocs/BT747%APP_LANG_EXT%.jar
ssh shell.sourceforge.net chmod 755 bt747/htdocs/BT747%APP_LANG_EXT%.jar
scp dist/BT747_rxtx.jar shell.sourceforge.net:bt747/htdocs/BT747_rxtx%APP_LANG_EXT%.jar
ssh shell.sourceforge.net chmod 755 bt747/htdocs/BT747_rxtx%APP_LANG_EXT%.jar
goto end:

:jnlp
REM Next line to upload Java web start specification
ssh shell.sourceforge.net chmod 755 bt747/htdocs/BT747.jnlp
scp webstart/BT747.jnlp shell.sourceforge.net:bt747/htdocs/BT747.jnlp
ssh shell.sourceforge.net chmod 755 bt747/htdocs/BT747.jnlp
scp webstart/BT747_win32comm.jnlp shell.sourceforge.net:bt747/htdocs/BT747_win32comm.jnlp
goto end:

:fonts
scp webstart/WabaKor.jar shell.sourceforge.net:bt747/htdocs/WabaKor.jar
ssh shell.sourceforge.net chmod 755 bt747/htdocs/WabaChi.jar
scp webstart/WabaChi.jar shell.sourceforge.net:bt747/htdocs/WabaChi.jar
ssh shell.sourceforge.net chmod 755 bt747/htdocs/WabaKor.jar
:japfonts
scp webstart/WabaJap.jar shell.sourceforge.net:bt747/htdocs/WabaJap.jar
ssh shell.sourceforge.net chmod 755 bt747/htdocs/WabaJap.jar
goto end:

:end

ENDLOCAL
