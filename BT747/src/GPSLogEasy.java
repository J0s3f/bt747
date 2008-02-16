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

import javax.swing.border.TitledBorder;

import gps.BT747_dev;
import gps.GPSstate;
import gps.GpsEvent;
import gps.settings;

import bt747.Txt;
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
      private Button m_btStore;
      private Button m_btRestore;
      private Button m_btHotStart;
      private Button m_btWarmStart;
      private Button m_btColdStart;
      private Button m_btFullColdStart;

      private Button [] chkRCR =new Button[BT747_dev.C_RCR_COUNT];

      private Button m_btForceErase;
      
      private Label lbLogUserTxt;
      
      private AppSettings m_settings;
      
      public GPSLogEasy(GPSstate state, AppSettings settings) {
          m_GPSstate= state;
          m_settings = settings;
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
      
      /** Options for the first warning message */
      private static final String[] C_EraseOrCancel = {
              Txt.ERASE, Txt.CANCEL
      };
      /** Options for the second warning message - reverse order on purpose */
      private static final String[] C_CancelConfirmErase = {
              Txt.CANCEL, Txt.CONFIRM_ERASE
      };

      
      private void forceErase() {
          /** Object to open multiple message boxes */
          MessageBox m_mb; 
          m_mb=new MessageBox(
                  Txt.TITLE_ATTENTION,
                  Txt.C_msgEraseWarning,
                  C_EraseOrCancel);
          m_mb.popupBlockingModal();
          if(m_mb.getPressedButtonIndex()==0) {
              m_mb=new MessageBox(
                      Txt.TITLE_ATTENTION,
                      Txt.C_msgEraseWarning2,
                      C_CancelConfirmErase);
              m_mb.popupBlockingModal();
              if(m_mb.getPressedButtonIndex()==1) {
                  // Erase log
                  m_GPSstate.recoveryEraseLog();
              }
          }
      }
      
      
      // "Volatile" settings of the MTK loggers:
      //  - Log conditions;
      //      - Time, Speed, Distance[3x 4 byte]
      //  - Log format;              [4 byte]
      //  - Fix period               [4 byte]
      //  - SBAS /DGPS / TEST SBAS         [byte, byte, byte]
      //  - Log overwrite/STOP       [byte, byte]
      //  - NMEA output              [18 byte]
      private void StoreSetting1() {
          m_settings.setTimeConditionSetting1(m_GPSstate.logTimeInterval);
          m_settings.setDistConditionSetting1(m_GPSstate.logDistanceInterval);
          m_settings.setSpeedConditionSetting1(m_GPSstate.logSpeedInterval);
          m_settings.setLogFormatConditionSetting1(m_GPSstate.logFormat);
          m_settings.setFixSetting1(m_GPSstate.logFix);
          m_settings.setSBASSetting1(m_GPSstate.SBASEnabled);
          m_settings.setDGPSSetting1(m_GPSstate.dgps_mode);
          m_settings.setTestSBASSetting1(m_GPSstate.SBASTestEnabled);
          m_settings.setLogOverwriteSetting1(m_GPSstate.logFullOverwrite);
          String NMEA="";
          for (int i=0;i<BT747_dev.C_NMEA_SEN_COUNT;i++) {
              NMEA+=(m_GPSstate.NMEA_periods[i]);
          }
          m_settings.setNMEASetting1(NMEA);
      }
      
      private void RestoreSetting1() {
          m_GPSstate.setLogTimeInterval(m_settings.getTimeConditionSetting1());
          m_GPSstate.setLogDistanceInterval(m_settings.getDistConditionSetting1());
          m_GPSstate.setLogSpeedInterval(m_settings.getSpeedConditionSetting1());
          m_GPSstate.setLogFormat(m_settings.getLogFormatSetting1());
          m_GPSstate.setFixInterval(m_settings.getFixSetting1());
          m_GPSstate.setSBASEnabled(m_settings.getSBASSetting1());
          m_GPSstate.setSBASTestEnabled(m_settings.getTestSBASSetting1());
          m_GPSstate.setDGPSMode(m_settings.getDPGSSetting1());
          m_GPSstate.setLogOverwrite(m_settings.getLogOverwriteSetting1());
        
          String NMEA=m_settings.getNMEASetting1();
          int[] Periods = new int[BT747_dev.C_NMEA_SEN_COUNT];
          
          for (int i=0;i<BT747_dev.C_NMEA_SEN_COUNT;i++) {
              Periods[i]=(int)(NMEA.charAt(i)-'0');
          }
          m_GPSstate.setNMEAPeriods(Periods);
          m_GPSstate.getNMEAPeriods();
      }

      private void getSettings() {
          m_GPSstate.getLogReasonStatus();
          m_GPSstate.getLogFormat();
          m_GPSstate.getFixInterval();
          m_GPSstate.getSBASEnabled();
          m_GPSstate.getSBASTestEnabled();
          m_GPSstate.getDGPSMode();
          m_GPSstate.getLogOverwrite();
          m_GPSstate.getNMEAPeriods();
      }
      

      private void enableStore() {
          m_btStore.setEnabled(m_GPSstate.isDataOK(
                  GPSstate.C_OK_FIX        |
                  GPSstate.C_OK_DGPS       |
                  GPSstate.C_OK_SBAS       |
                  GPSstate.C_OK_NMEA       |
                  GPSstate.C_OK_SBAS_TEST  |
                  // GPSstate.C_OK_SBAS_DATUM |
                  GPSstate.C_OK_TIME       |
                  GPSstate.C_OK_SPEED      |
                  GPSstate.C_OK_DIST       |
                  GPSstate.C_OK_FORMAT)
          );
          m_btRestore.setEnabled(m_settings.isStoredSetting1());
      }
      
      public void onEvent(Event event) {
          super.onEvent(event);
          switch (event.type) {
          case ControlEvent.PRESSED:
              event.consumed=true;
              if(event.target==this) {
                  getSettings();
              } else if(event.target==m_btSet2Hz) {
                  m_GPSstate.setFixInterval(500);
              } else if (event.target==m_btSet5Hz) {
                  m_GPSstate.setLogTimeInterval(2);
                  m_GPSstate.setFixInterval(200);
              } else if (event.target==m_btStore) {
                  StoreSetting1();
              } else if (event.target==m_btRestore) {
                  RestoreSetting1();
              } else if (event.target==m_btHotStart) {
                  m_GPSstate.doHotStart();
              } else if (event.target==m_btColdStart) {
                  m_GPSstate.doColdStart();
              } else if (event.target==m_btWarmStart) {
                  m_GPSstate.doWarmStart();
              } else if (event.target==m_btFullColdStart) {
                  MessageBox mb;
                  String []szExitButtonArray = {Txt.YES,Txt.NO};
                  mb = new MessageBox(Txt.TITLE_ATTENTION,
                          Txt.CONFIRM_FACT_RESET,
                          szExitButtonArray);                                   
                  mb.popupBlockingModal();                                      
                  if (mb.getPressedButtonIndex()==0){
                      // Exit application
                      m_GPSstate.doFullColdStart();
                  }
              } else if (event.target==m_btForceErase) {
                  forceErase();
              } else {
                  for (int i=0;i<BT747_dev.C_RCR_COUNT;i++) {
                      if (event.target==chkRCR[i]) {
                          m_GPSstate.logImmediate(1<<i);
                      }
                  }
                  
                  event.consumed=false;
              }
              break;
          case GpsEvent.DATA_UPDATE:
              if(event.target==this) {
                  enableStore();
              }
                  
              //                  updateLogFormat(m_GPSstate.logFormat);
              event.consumed=true;
              break;
      }
  }
}
