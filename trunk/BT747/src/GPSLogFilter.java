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
import waba.ui.PushButtonGroup;

import gps.BT747_dev;
import gps.GPSFilter;

import bt747.Txt;
import bt747.ui.Check;

/** The purpose of this container is to set the log filter settings.
 */
public class GPSLogFilter extends Container {
    private GPSFilter[] m_logFilters;
    private GPSFilter[] m_logFiltersAdv;
    private int currentLogFilter=0;
    private AppSettings m_settings;

    public GPSLogFilter(AppSettings settings, final GPSFilter[] logFilters,
            final GPSFilter[] logFiltersAdv) {
        m_logFilters = logFilters;
        m_logFiltersAdv = logFiltersAdv;
        m_settings=settings;
    }

    private String[] strValid= Txt.STR_VALID;
    private String[] C_PB_TYPE_NAMES=new String[2];


    private static final int C_VALID_COUNT=9;
    private Check [] chkValid =new Check[C_VALID_COUNT];
    private PushButtonGroup pbPtType;
    
    private void getSettings(GPSFilter[] logFilters) {
        logFilters[GPSFilter.C_TRKPT_IDX].setRcrMask(m_settings.getTrkPtRCR());
        logFilters[GPSFilter.C_TRKPT_IDX].setValidMask(m_settings.getTrkPtValid());
        logFilters[GPSFilter.C_WAYPT_IDX].setRcrMask(m_settings.getWayPtRCR());
        logFilters[GPSFilter.C_WAYPT_IDX].setValidMask(m_settings.getWayPtValid());
    };
    
    private void getSettings() {
        getSettings(m_logFilters);
        getSettings(m_logFiltersAdv);
    }

    public void onStart() {
        getSettings();
        C_PB_TYPE_NAMES[GPSFilter.C_TRKPT_IDX]=Txt.TRKPT;
        C_PB_TYPE_NAMES[GPSFilter.C_WAYPT_IDX]=Txt.WAYPT;
        add(pbPtType=
            new PushButtonGroup(
                    C_PB_TYPE_NAMES, // labes for buttons
                    true, // atleastone
                    0,1,2,1,true, // selected, gap, insidegap, rows, allsamewidth
                    PushButtonGroup.NORMAL  // Only one selected at a time
                    )
                    , CENTER, TOP+2);

        // Add all tick buttons.
        for (int i = 0; i < C_VALID_COUNT; i++) {
            chkValid[i] = new Check(strValid[i]);
            add(chkValid[i],
                ((i==0) ? LEFT :((i == ((C_VALID_COUNT / 2))) ? getClientRect().width/2:SAME)),
                ((i==0) || (i == ((C_VALID_COUNT / 2)))) ? AFTER+2:AFTER-1,
                ((i==0) || (i == ((C_VALID_COUNT / 2))))?(Control)pbPtType:(Control)chkValid[i-1]
            );
            chkValid[i].setEnabled(true);
        }
        // Add all tick buttons.
        int x=LEFT;
        int y=SAME;
        Control rel=null;
        final int RCR_COL=4;
        for (int i=0; i<BT747_dev.C_RCR_COUNT; i++) {
            chkRCR[i]= new Check(BT747_dev.C_STR_RCR[i]);
            //add( chkRCR[i], LEFT, AFTER);
            if(i == 0) {
                x=LEFT;
            } else if((i%(BT747_dev.C_RCR_COUNT / RCR_COL)) ==0) {
                x=getClientRect().width*(i/(BT747_dev.C_RCR_COUNT / RCR_COL))/RCR_COL+8;
            }

            if((i%(BT747_dev.C_RCR_COUNT / RCR_COL)) ==0) {
                rel=(Control)chkValid[C_VALID_COUNT-1];
                y=AFTER+6;
            } else {
                y=AFTER-1;
            }
            add(chkRCR[i],x,y,rel);
                            
            rel=chkRCR[i];
            chkRCR[i].setEnabled(true);
        }
    }

    /** Get the format set by the user in the user interface. */
    private int getValid() {
        int bitMask=1;
        int valid=0;
        for (int i=0;i<C_VALID_COUNT;i++) {
            if(chkValid[i].getChecked()) {
                valid|=bitMask;
            }
            bitMask<<=1;
        }
        return valid;
    }
    
    /** Updates the format options shown the the user.<br>
     * This is typically done when the device responded with the current settings.
     * @param p_logFormat Valid to set
     */
    private void setValid(final int valid) {
        int bitMask=1;
        //if(GPS_DEBUG) {   waba.sys.Vm.debug("UPD:"+Convert.unsigned2hex(p_logFormat,2)+"\n");}
        
        
        for (int i=0; i<C_VALID_COUNT; i++) {
            chkValid[i].setChecked((valid & bitMask)!=0);
//            chkValid[i].repaintNow();
            bitMask<<=1;
        }
    }

    private Check [] chkRCR =new Check[BT747_dev.C_RCR_COUNT];

    /** Get the format set by the user in the user interface. */
    private int getRCR() {
        int bitMask=1;
        int rcrMask=0;
        for (int i=0;i<BT747_dev.C_RCR_COUNT;i++) {
            if(chkRCR[i].getChecked()) {
                rcrMask|=bitMask;
            }
            bitMask<<=1;
        }
        return rcrMask;
    }
    
    /** Updates the format options shown the the user.<br>
     * This is typically done when the device responded with the current settings.
     * @param p_logFormat RCR to set
     */
    private void setRCR(final int valid) {
        int bitMask=1;
        //if(GPS_DEBUG) {   waba.sys.Vm.debug("UPD:"+Convert.unsigned2hex(p_logFormat,2)+"\n");}
        
        
        for (int i=0;i<BT747_dev.C_RCR_COUNT;i++) {
            chkRCR[i].setChecked((valid & bitMask)!=0);
//            chkRCR[i].repaintNow();
            bitMask<<=1;
        }
    }
    
    private void updateFromFilter() {
        setValid(m_logFilters[currentLogFilter].getValidMask());
        setRCR(m_logFilters[currentLogFilter].getRcrMask());
    }

    
    /** Handle events for this object.
     * @param event The event to be interpreted.
     */
    public void onEvent( Event event ) {
        switch (event.type) {
        case ControlEvent.PRESSED:
            if (event.target==this) {
                // Tab is selected
                updateFromFilter();
                event.consumed=true;
            } else if (event.target==null) {
                // Update from GPSState
            } else if (event.target==pbPtType) {
                currentLogFilter=pbPtType.getSelected();
                updateFromFilter();
            } else {
                boolean z_updated=false;
                for (int i=0;i<C_VALID_COUNT;i++) {
                    if (event.target==chkValid[i]) {
                        z_updated=true;
                    }
                }
                if(z_updated) {
                    m_logFilters[currentLogFilter].setValidMask(getValid());
                    switch (currentLogFilter) {
                    case GPSFilter.C_TRKPT_IDX:
                        m_settings.setTrkPtValid(getValid());
                        break;
                    case GPSFilter.C_WAYPT_IDX:
                        m_settings.setWayPtValid(getValid());
                        break;
                    }
                }
                z_updated=false;
                for (int i=0;i<BT747_dev.C_RCR_COUNT;i++) {
                    if (event.target==chkRCR[i]) {
                        z_updated=true;
                    }
                }
                if(z_updated) {
                    m_logFilters[currentLogFilter].setRcrMask(getRCR());
                    switch (currentLogFilter) {
                    case GPSFilter.C_TRKPT_IDX:
                        m_settings.setTrkPtRCR(getRCR());
                        break;
                    case GPSFilter.C_WAYPT_IDX:
                        m_settings.setWayPtRCR(getRCR());
                        break;
                    }
                }
                getSettings();
            }
        break;
        }
    }

}
