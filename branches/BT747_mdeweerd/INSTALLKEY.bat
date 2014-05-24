@echo off
setlocal
set       MYROOTPATH=%~dp0%
set           MYDIST=%MYROOTPATH%dist
set            MYLIB=%MYROOTPATH%lib
set         RXTXPATH=%MYLIB%\rxtx-2.1-7-bins-r2
set MYSYSTEMRXTXBINS=%RXTXPATH%\Windows\i368-mingw32
set             PATH=%MYSYSTEMRXTXBINS%;%MYROOTPATH%;%JAVA_HOME%\bin;%PATH%
set        CLASSPATH=%MYDIST%\BT747_j2se.jar;%RXTXPATH%\RXTXcomm.jar;%MYDIST%\libBT747.jar;%MYLIB%\swing-layout-1.0.3.jar;%MYLIB%\jopt-simple-3.1.jar;%MYLIB%\jchart2d-3.1.0.jar;%CLASSPATH%

REM java -Dbt747_prefix="COM" -Dbt747_settings="bt747settings.pdb" waba.applet.Applet BT747

echo -----BEGIN CERTIFICATE-----> key.cer
echo MIIDGjCCAtgCBEZOwqAwCwYHKoZIzjgEAwUAMHMxEDAOBgNVBAYTB1Vua25vd24xEDAOBgNVBAgT>> key.cer
echo B1Vua25vd24xEDAOBgNVBAcTB1Vua25vd24xEDAOBgNVBAoTB1Vua25vd24xEDAOBgNVBAsTB1Vu>> key.cer
echo a25vd24xFzAVBgNVBAMTDk1hcmlvIERFIFdFRVJEMB4XDTA3MDUxOTA5MjU1MloXDTA3MDgxNzA5>> key.cer
echo MjU1MlowczEQMA4GA1UEBhMHVW5rbm93bjEQMA4GA1UECBMHVW5rbm93bjEQMA4GA1UEBxMHVW5r>> key.cer
echo bm93bjEQMA4GA1UEChMHVW5rbm93bjEQMA4GA1UECxMHVW5rbm93bjEXMBUGA1UEAxMOTWFyaW8g>> key.cer
echo REUgV0VFUkQwggG3MIIBLAYHKoZIzjgEATCCAR8CgYEA/X9TgR11EilS30qcLuzk5/YRt1I870QA>> key.cer
echo wx4/gLZRJmlFXUAiUftZPY1Y+r/F9bow9subVWzXgTuAHTRv8mZgt2uZUKWkn5/oBHsQIsJPu6nX>> key.cer
echo /rfGG/g7V+fGqKYVDwT7g/bTxR7DAjVUE1oWkTL2dfOuK2HXKu/yIgMZndFIAccCFQCXYFCPFSML>> key.cer
echo zLKSuYKi64QL8Fgc9QKBgQD34aCF1ps93su8q1w2uFe5eZSvu/o66oL5V0wLPQeCZ1FZV4661FlP>> key.cer
echo 5nEHEIGAtEkWcSPoTCgWE7fPCTKMyKbhPBZ6i1R8jSjgo64eK7OmdZFuo38L+iE1YvH7YnoBJDvM>> key.cer
echo pPG+qFGQiaiD3+Fa5Z8GkotmXoB7VSVkAUw7/s9JKgOBhAACgYAfPttInGK5B4JAc4EpexLyH6f3>> key.cer
echo GTK1p/tAnZpDnnW9k6c4JXF80NQMQR2wm26nDj47bhZ43Cwv2N+JtXq+vlNEqHrW7CKRCTus2NJS>> key.cer
echo boUkDLinuv8m/L5eV/nbFOZhOVxVu+6Wlxs8wo9LBX9idljGJZoa95G4739RtsUp+apo8jALBgcq>> key.cer
echo hkjOOAQDBQADLwAwLAIURSgERAGTBIv5N0xyl+KJR8nIxHYCFF8WgroBwevtBuFlw5usiJkoOcPf>> key.cer
echo -----END CERTIFICATE----->> key.cer

keytool -importcert -keystore %USERPROFILE%\AppData\LocalLow\Sun\Java\Deployment\security\trusted.certs -alias bt747 -file key.cer -storepass ""
