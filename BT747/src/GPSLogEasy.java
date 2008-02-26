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
import waba.ui.Container;
import waba.ui.Control;
import waba.ui.ControlEvent;
import waba.ui.Event;
import waba.ui.Label;
import waba.ui.MessageBox;

import gps.BT747_dev;
import gps.GPSstate;

import bt747.ui.Button;

/** Implement some buttons to easily do more complex operations
 * or to do some things not done in other tabs.
 * 
 * @author Mario De Weerd
 */
public class GPSLogEasy extends Container {
      private GPSstate m_GPSstate;
      
      private Button m_btSet5Hz;
      private Button m_btSet2Hz;
      private Button m_btHotStart;
      private Button m_btWarmStart;
      private Button m_btColdStart;
      private Button m_btFullColdStart;

      private Button [] chkRCR =new Button[BT747_dev.C_RCR_COUNT];

      private Button m_btForceErase;
      
      private Label lbLogUserTxt;
      
      public GPSLogEasy(GPSstate state) {
          m_GPSstate= state;
      }
      
      protected void onStart() {
          add(m_btSet5Hz = new Button("5Hz fix and log"), CENTER, AFTER+3); //$NON-NLS-1$
          add(m_btSet2Hz = new Button("2Hz fix (avoid static nav)"), CENTER, AFTER+3); //$NON-NLS-1$
          add(m_btHotStart = new Button("Hot start"), LEFT, AFTER+10); //$NON-NLS-1$
          add(m_btWarmStart = new Button("Warm start"), CENTER, SAME); //$NON-NLS-1$
          add(m_btColdStart = new Button("Cold start"), RIGHT, SAME); //$NON-NLS-1$
          add(m_btFullColdStart = new Button("Factory reset"), LEFT, AFTER+2); //$NON-NLS-1$

          add(m_btForceErase = new Button("Forced erase"), RIGHT, SAME); //$NON-NLS-1$

          add(lbLogUserTxt=new Label("Click to log a point with reason:"),LEFT,AFTER+2);
          // Add all tick buttons.
          int x=LEFT;
          int y=SAME;
          Control rel=null;
          final int RCR_COL=4;
          for (int i=0; i<BT747_dev.C_RCR_COUNT; i++) {
              chkRCR[i]= new Button(BT747_dev.C_STR_RCR[i]);
              //add( chkRCR[i], LEFT, AFTER);
              if(i == 0) {
                  x=LEFT;
              } else if((i%(BT747_dev.C_RCR_COUNT / RCR_COL)) ==0) {
                  x=getClientRect().width*(i/(BT747_dev.C_RCR_COUNT / RCR_COL))/RCR_COL+8;
              }

              if((i%(BT747_dev.C_RCR_COUNT / RCR_COL)) ==0) {
                  rel=lbLogUserTxt;
                  y=AFTER+6;
              } else {
                  y=AFTER-1;
              }
              add(chkRCR[i],x,y,rel);
                              
              rel=chkRCR[i];
              chkRCR[i].setEnabled(true);
          }
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
          } else if (event.target==m_btForceErase) {
              m_GPSstate.recoveryEraseLog();
          } else {
              for (int i=0;i<BT747_dev.C_RCR_COUNT;i++) {
                  if (event.target==chkRCR[i]) {
                      m_GPSstate.logImmediate(1<<i);
                  }
              }

              event.consumed=false;
          }
          break;
      }
  }
}
