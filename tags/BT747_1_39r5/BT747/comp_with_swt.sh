#gcj -fjni -c -g0 webstart/Waba_only.jar
#ar -rcs libWaba_only.a Waba_only.o 
#ranlib libWaba_only.a 
#gcj -fjni -c -g0 webstart/RXTXcomm.jar
#ar -rcs libRXTX.a RXTXcomm.o 
#gcj -fjni -c -g0 webstart/RXTXcomm.jar
#ar -rcs libRXTX.a RXTXcomm.o 
#gcj -mwindows -fjni --classpath=./swt/swt.jar:webstart/Waba_only.jar:webstart/RXTXcomm.jar  --main=waba.applet.Applet dist/BT747_rxtx.jar -L. -lswt -lWaba_only -lRXTX
unset CLASSPATH
gcj -fjni --classpath=./swt/swt.jar:webstart/Waba_only.jar:webstart/RXTXcomm.jar  --main=waba.applet.Applet dist/BT747_rxtx.jar -L. -lswt -lWaba_only -lRXTX
