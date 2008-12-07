//********************************************************************
//***                           BT 747                             ***
//***                      April 14, 2007                          ***
//***                  (c)2007 Mario De Weerd                      ***
//***                     m.deweerd@ieee.org                       ***
//***  **********************************************************  ***
//***  Software is provided "AS IS," without a warranty of any     ***
//***  kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
//***  INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS  ***
//***  FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY    ***
//***  EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
//***  IS ASSUMED BY THE USER. See the GNU General Public License  ***
//***  for more details.                                           ***
//***  *********************************************************** ***
//***  The application was written using the SuperWaba toolset.    ***
//***  This is a proprietary development environment based in      ***
//***  part on the Waba development environment developed by       ***                                   
//***  WabaSoft, Inc.                                              ***
//********************************************************************  
import waba.ui.Button;
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Event;
import waba.ui.MessageBox;

import gps.GPSstate;
/*
 * Created on 19 ao�t 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GPSLogEasy extends Container {
      final private static boolean ENABLE_PWR_SAVE_CONTROL=false;
      private GPSstate m_GPSstate;
      
      private Button m_btSet5Hz;
      private Button m_btSet2Hz;
      private Button m_btHotStart;
      private Button m_btWarmStart;
      private Button m_btColdStart;
      private Button m_btFullColdStart;
      
      public GPSLogEasy(GPSstate state) {
          m_GPSstate= state;
      }
      
      protected void onStart() {
          add(m_btSet5Hz = new Button("5Hz fix and log"), CENTER, AFTER+3); //$NON-NLS-1$
          add(m_btSet2Hz = new Button("2Hz fix (avoid static nav)"), CENTER, AFTER+3); //$NON-NLS-1$
          add(m_btHotStart = new Button("Hot start"), LEFT, AFTER+10); //$NON-NLS-1$
          add(m_btWarmStart = new Button("Warm start"), CENTER, SAME); //$NON-NLS-1$
          add(m_btColdStart = new Button("Cold start"), RIGHT, SAME); //$NON-NLS-1$
          add(m_btFullColdStart = new Button("Factory reset"), CENTER, AFTER+2); //$NON-NLS-1$
      }
      
      
      public void onEvent(Event event) {
          super.onEvent(event);
          switch (event.type) {
          case ControlEvent.PRESSED:
              event.consumed=true;
          if(event.target==m_btSet2Hz) {
              m_GPSstate.setFixInterval(500);
          } else if (event.target==m_btSet5Hz) {
              m_GPSstate.setLogTimeInterval(2);
              m_GPSstate.setFixInterval(200);
          } else if (event.target==m_btHotStart) {
              m_GPSstate.doHotStart();
          } else if (event.target==m_btColdStart) {
              m_GPSstate.doColdStart();
          } else if (event.target==m_btWarmStart) {
              m_GPSstate.doWarmStart();
          } else if (event.target==m_btFullColdStart) {
              MessageBox mb;
              String []szExitButtonArray = {"Yes","No"};
              mb = new MessageBox("Attention",
                      "You are about to perform a factory|" +
                      "reset of your GPS Logger Device.|"+
                      "||Do you confirm this reset at|"+
                      "your own risk ???",
                      szExitButtonArray);                                   
              mb.popupBlockingModal();                                      
              if (mb.getPressedButtonIndex()==0){
                  // Exit application
                  m_GPSstate.doFullColdStart();
              }
          } else {
              event.consumed=false;
          }
          break;
      }
  }
}