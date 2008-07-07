package bt747.waba_view;
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
import waba.ui.Control;
import waba.ui.ControlEvent;
import waba.ui.Event;
import waba.ui.Label;
import waba.ui.MessageBox;

import gps.BT747Constants;
import gps.GpsEvent;

import bt747.Txt;
import bt747.model.Controller;
import bt747.model.Model;

/** Implement some buttons to easily do more complex operations
 * or to do some things not done in other tabs.
 * 
 * @author Mario De Weerd
 */
public class GPSLogEasy extends Container {
      private Button m_btSet5Hz;
      private Button m_btSet2Hz;
      private Button m_btStore;
      private Button m_btRestore;
      private Button m_btHotStart;
      private Button m_btWarmStart;
      private Button m_btColdStart;
      private Button m_btFullColdStart;

      private Button [] chkRCR =new Button[BT747Constants.C_RCR_COUNT];

      private Button m_btForceErase;
      
      private Label lbLogUserTxt;
      
      private Model m;
      private Controller c;
      
      public GPSLogEasy(Model m, Controller c) {
          this.m = m;
          this.c = c;
      }
      
      protected void onStart() {
          add(m_btSet5Hz = new Button(Txt.BT_5HZ_FIX), LEFT, AFTER+3); //$NON-NLS-1$
          add(m_btSet2Hz = new Button(Txt.BT_2HZ_FIX), RIGHT, SAME); //$NON-NLS-1$
          add(m_btStore = new Button(Txt.STORE_SETTINGS), LEFT, AFTER+3); //$NON-NLS-1$
          add(m_btRestore = new Button(Txt.RESTORE_SETTINGS), RIGHT, SAME); //$NON-NLS-1$
          enableStore();
          add(m_btHotStart = new Button(Txt.BT_HOT), LEFT, AFTER+10); //$NON-NLS-1$
          add(m_btWarmStart = new Button(Txt.BT_WARM), CENTER, SAME); //$NON-NLS-1$
          add(m_btColdStart = new Button(Txt.BT_COLD), RIGHT, SAME); //$NON-NLS-1$
          add(m_btFullColdStart = new Button(Txt.BT_FACT_RESET), LEFT, AFTER+2); //$NON-NLS-1$

          add(m_btForceErase = new Button(Txt.BT_FORCED_ERASE), RIGHT, SAME); //$NON-NLS-1$

          add(lbLogUserTxt=new Label(Txt.BT_PT_WITH_REASON),LEFT,AFTER+2);
          // Add all tick buttons.
          int x=LEFT;
          int y=SAME;
          Control rel=null;
          final int RCR_COL=4;
          for (int i=0; i<BT747Constants.C_RCR_COUNT; i++) {
              chkRCR[i]= new Button(BT747Constants.C_STR_RCR[i]);
              //add( chkRCR[i], LEFT, AFTER);
              if(i == 0) {
                  x=LEFT;
              } else if((i%(BT747Constants.C_RCR_COUNT / RCR_COL)) ==0) {
                  x=getClientRect().width*(i/(BT747Constants.C_RCR_COUNT / RCR_COL))/RCR_COL+8;
              }

              if((i%(BT747Constants.C_RCR_COUNT / RCR_COL)) ==0) {
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
           
      private void enableStore() {
          // TODO : should enable this from controller.
          m_btStore.setEnabled(c.isEnableStoreOK());
          m_btRestore.setEnabled(m.isStoredSetting1());
      }
      
      public void onEvent(Event event) {
          super.onEvent(event);
          switch (event.type) {
          case ControlEvent.PRESSED:
              event.consumed=true;
              if(event.target==this) {
                  c.reqSettingsForStorage();
              } else if(event.target==m_btSet2Hz) {
                  c.setFixInterval(500);
              } else if (event.target==m_btSet5Hz) {
                  c.setLogTimeInterval(2);
                  c.setFixInterval(200);
              } else if (event.target==m_btStore) {
                  c.StoreSetting1();
              } else if (event.target==m_btRestore) {
                  c.RestoreSetting1();
              } else if (event.target==m_btHotStart) {
                  c.doHotStart();
              } else if (event.target==m_btColdStart) {
                  c.doColdStart();
              } else if (event.target==m_btWarmStart) {
                  c.doWarmStart();
              } else if (event.target==m_btFullColdStart) {
                  MessageBox mb;
                  String []szExitButtonArray = {Txt.YES,Txt.NO};
                  mb = new MessageBox(Txt.TITLE_ATTENTION,
                          Txt.CONFIRM_FACT_RESET,
                          szExitButtonArray);                                   
                  mb.popupBlockingModal();                                      
                  if (mb.getPressedButtonIndex()==0){
                      // Exit application
                      c.doFullColdStart();
                  }
              } else if (event.target==m_btForceErase) {
                  c.forceErase();
              } else {
                  for (int i=0;i<BT747Constants.C_RCR_COUNT;i++) {
                      if (event.target==chkRCR[i]) {
                          c.logImmediate(1<<i);
                      }
                  }
                  event.consumed=false;
              }
              break;
          default:
              if(event.type==GpsEvent.DATA_UPDATE) {
                  if(event.target==this) {
                      enableStore();
                  }
                  event.consumed=true;
              }
      }
  }
}
