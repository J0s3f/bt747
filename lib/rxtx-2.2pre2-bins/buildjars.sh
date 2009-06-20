CDIR=$PWD
(cd ../../.. ; ./sign.bat $CDIR/*.jar)
exit
(cd i686-pc-linux-gnu ; zip ../rxtx_2_2p2_lin_i686.jar *)
(cd mac-10.5 ; zip ../rxtx_2_2p2_mac_10_5.jar *)
#(cd sparc-sun-solaris2.10-32 ; zip ../rxtx_2_2p2_sol32.jar *)
#(cd sparc-sun-solaris2.10-64 ; zip ../rxtx_2_2p2_sol64.jar *)
(cd win32 ; zip ../rxtx_2_2p2_win32.jar *)
(cd win64 ; zip ../rxtx_2_2p2_win64.jar *)
(cd x86_64-unknown-linux-gnu ; zip ../rxtx_2_2p2_x86_64.jar *)
