/**
 * 
 */
package bt747.j2se_view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import org.mozilla.webclient.BrowserControl;
import org.mozilla.webclient.BrowserControlCanvas;
import org.mozilla.webclient.BrowserControlFactory;
import org.mozilla.webclient.CurrentPage;
import org.mozilla.webclient.DocumentLoadListener;
import org.mozilla.webclient.EventRegistration2;
import org.mozilla.webclient.Navigation;
import org.mozilla.webclient.WebclientEvent;
import org.mozilla.webclient.impl.BrowserControlImpl;
import org.mozilla.webclient.impl.wrapper_native.WrapperFactoryImpl;

import bt747.sys.Generic;

/**
 * @author Mario
 * 
 */
public class MyWebClient extends JPanel
implements MouseListener, DocumentLoadListener, KeyListener {

    /**
     * 
     */
    private static final long serialVersionUID = 6553876459223895252L;

    WrapperFactoryImpl wf;
    BrowserControl browserControl;
    BrowserControlCanvas browser;


    Navigation navigation;
    CurrentPage currentPage;
    //History hi;
    
    /**
     * 
     */
    public MyWebClient() {
    }
    /* (non-Javadoc)
     * @see javax.swing.JComponent#addNotify()
     */
    @Override
    public void addNotify() {
        super.addNotify();
        // See http://www.mozilla.org/projects/blackwood/webclient/ref_guides/Implementation_guide/index.htm
        // See http://mxr.mozilla.org/mozilla/source/java/webclient/classes_spec/org/mozilla/webclient/test/EMWindow.java
        String binDir = "C:\\Program Files\\Mozilla Firefox";
        String javaLibPath = System.getProperty("java.library.path");
        
        javaLibPath+=";"+binDir+";C:\\Windows";
        System.setProperty("java.library.path", javaLibPath);
        try {
            BrowserControlFactory.setAppData(binDir);
            browserControl = BrowserControlFactory.newBrowserControl();
        } catch (Exception e) {
            Generic.debug("Can't create BrowserControl: ",e);
            return;
            // TODO: handle exception
        }
        try {
            browser = 
            (BrowserControlCanvas) (browserControl.queryInterface(BrowserControl.BROWSER_CONTROL_CANVAS_NAME));
            add(browser,      BorderLayout.CENTER);
            browser.setSize(300,300);
            
            navigation = (Navigation) (browserControl.queryInterface(BrowserControl.NAVIGATION_NAME));
            currentPage = (CurrentPage) (browserControl.queryInterface(BrowserControl.CURRENT_PAGE_NAME));
            
            
            EventRegistration2 er = (EventRegistration2) browserControl.queryInterface(BrowserControl.EVENT_REGISTRATION_NAME);
            er.addDocumentLoadListener(this);
            er.addMouseListener(this);
            er.addKeyListener(this);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    
    
    public void actionPerformed (ActionEvent evt) {
        String command = evt.getActionCommand();
        
//        try {
//            if (command.equals("Back")) {
//                if (browserControl.canBack()) {
//                    browserControl.back();
//                    int index = browserControl.getHistoryIndex();
//                    String newURL = browserControl.getURL(index);
//                    
//                    System.out.println(newURL);
//                    
//                    urlField.setText(newURL);
//                }
//            }
//            else if (command.equals("Forward")) {
//                if (browserControl.canForward()) {
//                    browserControl.forward();
//                    int index = browserControl.getHistoryIndex();
//                    String newURL = browserControl.getURL(index);
//                    
//                    System.out.println(newURL);
//                    
//                    urlField.setText(newURL);
//                }
//            }
//            else if (command.equals("Stop")) {
//                browserControl.stop();
//            }
//            else {
//                browserControl.loadURL(urlField.getText());
//            }
//        }
//        catch (Exception e) {
//            System.out.println(e.toString());
//        }
    } // actionPerformed()
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see org.mozilla.webclient.WebclientEventListener#eventDispatched(org.mozilla.webclient.WebclientEvent)
     */
    public void eventDispatched(WebclientEvent arg0) {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }
    /* (non-Javadoc)
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
        
    }
}
