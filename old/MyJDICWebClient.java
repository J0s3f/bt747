/**
 * 
 */
package bt747.j2se_view;
import java.awt.Frame;
import java.awt.Panel;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JPanel;

import org.jdesktop.jdic.browser.WebBrowser;

import bt747.sys.Generic;

/**
 * @author Mario
 *
 */
public class MyJDICWebClient extends JPanel {
    // Abandonned because project does not work on macosX
    
    // Alternative:http://djproject.sourceforge.net/ns/ (Win and Linux only).
    
    // also read. http://forums.java.net/jive/message.jspa?messageID=318889
    
    
// // JDICsample.pde
// // Marius Watz - http://workshop.evolutionzone.com
//
// 
// aBrowser browser;
// long last;
//
// // Set to true to demonstrate setContent instead of
// // loading URLs.
// boolean doRandomHTML=true;
// String rndWords[];
// int rndWordNum=0;
//
// void setup() {
//   size(200,200);
//   browser=new aBrowser();
//   browser.initPanel(1024,768);
//   if(!doRandomHTML) browser.setURL(”http://processing.org/”);
//   else initRandomText();
// }
//
// ////////////////////////////////////////////
// // Random HTML
//
// void draw() {
//   if(doRandomHTML && (millis()-last>3000)) {
//     String c1,c2,str;
//     c1=colorToHex(Random(200,255),random(150,200),0);
//     c2=colorToHex(random(50,100),random(50,100),0);
//
////     println(c1+” “+c2);
////     str="“+      “";
////     str+=""
//// <div style="width: 760px;">“;
////     for(int i=0; i< 30; i++) str+="
////
//// “+getRandLine()+”
////
//// “;
////     str+=”</div>
////
//// “;
//
//     browser.setContent(str);
//     last=millis();
//   }
// }
//
// String getRandLine() {
//   String s=”";
//
//   int num=(int)random(30,50);
//   for(int i=0; i< num; i++) s+=rndWords[(int)random(rndWordNum)]+" ";
//
//   return s;
// }
//
// void initRandomText() {
//   String lorem="Lorem ipsum dolor sit amet, pellentesque dolor"+
//    "a vestibulum, hendrerit augue lectus in libero dictumst et,"+
//    "condimentum gravida vestibulum litora semper. Lectus donec "+
//    "neque nunc cras molestie est, vel et. Pede inventore vestibulum "+
//    "justo est non nulla, lacus reiciendis rutrum phasellus nunc leo"+
//    "natoque. Urna ac id justo luctus lorem, ante viverra nam nam "+
//    "accusamus, aliquam metus vitae etiam sollicitudin erat ligula. "+
//    "Sodales tortor amet felis, sit risus mus vel sodales, cursus "+
//    "auctor, augue semper nam, diam eget. Vulputate ac viverra ante "+
//    "ipsum tristique ullamcorper, lacus nostra pharetra libero provident.";
//
//   rndWords=splitTokens(lorem);
//   rndWordNum=rndWords.length;
// }
//
// public static String colorToHex(float r,float g,float b) {
//   String s="";
//   if(r<16) s+="0"+Integer.toHexString((int)r);
//   else s+=Integer.toHexString((int)r);
//   if(g<16) s+="0"+Integer.toHexString((int)g);
//   else s+=Integer.toHexString((int)g);
//   if(b<16) s+="0"+Integer.toHexString((int)b);
//   else s+=Integer.toHexString((int)b);
//   s=s.toUpperCase();
//   return s;
// }
//
// ////////////////////////////////////////////
// // Convenience class for dealing with
// // the embedded browser engine.
//
// public class aBrowser {
   Frame frame;
   Panel panel;
   WebBrowser webBrowser;

   public MyJDICWebClient() throws MalformedURLException {
     // Set engine to IE
//     BrowserEngineManager mng=BrowserEngineManager.instance();
//     mng.setActiveEngine(BrowserEngineManager.IE);
       //Generic.debug(WebBrowserUtil.getBrowserPath());
       WebBrowser.setDebug(true);
       org.jdesktop.jdic.browser.internal.WebBrowserUtil.enableDebugMessages(true);
       
       URL url;
       // tries to open C:\devdir\BT747\"c:\program files\mozilla firefox\firefox.exe"  NAME INVALID    
       webBrowser = new WebBrowser(true);

     //add(webBrowser);

     org.jdesktop.layout.GroupLayout InfoPanelLayout = new org.jdesktop.layout.GroupLayout(this);
     this.setLayout(InfoPanelLayout);
     InfoPanelLayout.setHorizontalGroup(
         InfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
         .add(InfoPanelLayout.createSequentialGroup()
             //.addContainerGap()
             .add(webBrowser, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 628, Short.MAX_VALUE)
             //.addContainerGap()
             )
     );
     InfoPanelLayout.setVerticalGroup(
         InfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
         .add(InfoPanelLayout.createSequentialGroup()
             //.addContainerGap()
             .add(webBrowser, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
             //.addContainerGap()
             )
     );

     setURL("http://bt747.free.fr/x/s.html");
   }
//
//   public void initPanel(int w,int h) {
//     frame=new Frame("JIDCsample.pde");
//     frame.setLocation(50,50);
//     frame.setLayout(new BorderLayout());
//     //frame.setUndecorated(true);
//
//     //   Handle window close requests
//     frame.addWindowListener(new WindowAdapter( ) {
//       public void windowClosing(WindowEvent e) {System.exit(0);}
//     });
//
//     panel = new Panel();
//     panel.setLayout(new BorderLayout());
//     panel.setPreferredSize(new Dimension(w, h));
//     panel.add(webBrowser, BorderLayout.CENTER);
//     frame.add(panel,BorderLayout.CENTER);
//
//     Label status=new Label(
//       "JDICsample.pde - embedded web browser.");
//     status.setBackground(new Color(100,100,100));
//     status.setForeground(new Color(255,255,255));
//     status.setFont(new Font("Arial",Font.PLAIN,15));
//     status.setSize(600, 20);
//
//     frame.add(status,BorderLayout.SOUTH);
//     frame.pack();
//     frame.setVisible(true);
//   }
//
   public void setContent(String htmlContent) {
     webBrowser.setContent(htmlContent);
   }

   public void setURL(String url) {
     try {
       webBrowser.setURL(new URL(url));
       // Print out debug messages in the command line.
       //webBrowser.setDebug(false);
     }
     catch (MalformedURLException e) {
       Generic.debug(null,e);
       return;
     }
   }
}