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
import waba.ui.ControlEvent;
import waba.ui.Edit;
import waba.ui.Event;
import waba.ui.Label;
import waba.ui.PushButtonGroup;

import gps.GPSFilterAdvanced;
import gps.GpsEvent;

import bt747.Txt;
import bt747.sys.Convert;
import bt747.ui.Button;
/**
 * @author Mario De Weerd
 */
public class GPSLogFilterAdv extends Container {
    
    private Edit m_minRecCount;
    private Edit m_maxRecCount;
    private Edit m_minSpeed;
    private Edit m_maxSpeed;
    private Edit m_minDist;
    private Edit m_maxDist;
    private Edit m_maxPDOP;
    private Edit m_maxHDOP;
    private Edit m_maxVDOP;
    private Edit m_minNSAT;
    
    private GPSFilterAdvanced[] m_gpsFilters;
    
    
    private Button m_btSet;
    private Button m_btClear;

    private PushButtonGroup pbPtType;
    private String[] C_PB_TYPE_NAMES= {
            Txt.ACTIVE,
            Txt.INACTIVE
            };
    
    private AppSettings m_settings;
    
    public GPSLogFilterAdv(AppSettings settings, GPSFilterAdvanced[] filters) {;
        m_gpsFilters=filters;
        m_settings=settings;
    }
    
    protected void onStart() {
        super.onStart();
        
        add(m_minRecCount= new Edit(),LEFT, SAME);
        add(new Label(Txt.FLTR_REC),CENTER,SAME);
        add(m_maxRecCount= new Edit(),RIGHT, SAME);
        
        add(m_minSpeed= new Edit(),LEFT, AFTER);
        add(new Label(Txt.FLTR_SPD),CENTER,SAME);
        add(m_maxSpeed= new Edit(),RIGHT, SAME);
        
        add(m_minDist= new Edit(),LEFT, AFTER);
        add(new Label(Txt.FLTR_DST),CENTER,SAME);
        add(m_maxDist= new Edit(),RIGHT, SAME);
        
        add(new Label(Txt.FLTR_PDOP),CENTER,AFTER);
        add(m_maxPDOP= new Edit(),RIGHT, SAME);

        add(new Label(Txt.FLTR_HDOP),CENTER,AFTER);
        add(m_maxHDOP= new Edit(),RIGHT, SAME);
        
        add(new Label(Txt.FLTR_VDOP),CENTER,AFTER);
        add(m_maxVDOP= new Edit(),RIGHT, SAME);
        
        add(m_minNSAT= new Edit(),LEFT, AFTER);
        add(new Label(Txt.FLTR_NSAT),CENTER,SAME);

        String allowedKeys;
        allowedKeys=Edit.numbersSet+"-";
        m_minRecCount.setValidChars(allowedKeys);
        m_maxRecCount.setValidChars(allowedKeys);
        m_minNSAT.setValidChars(allowedKeys);
        allowedKeys+=".";
        m_minSpeed.setValidChars(allowedKeys);
        m_maxSpeed.setValidChars(allowedKeys);
        m_minDist.setValidChars(allowedKeys);
        m_maxDist.setValidChars(allowedKeys);
        m_maxPDOP.setValidChars(allowedKeys);
        m_maxHDOP.setValidChars(allowedKeys);
        m_maxVDOP.setValidChars(allowedKeys);

        add(new Label(Txt.IGNORE_0VALUES),CENTER,AFTER);

        
        m_btSet = new Button(Txt.SET);
        add(m_btSet, LEFT, AFTER+3); //$NON-NLS-1$
        m_btClear = new Button(Txt.CLEAR);
        add(m_btClear, AFTER, SAME); //$NON-NLS-1$

        add(pbPtType=
            new PushButtonGroup(
                    C_PB_TYPE_NAMES, // labes for buttons
                    true, // atleastone
                    0,1,2,1,true, // selected, gap, insidegap, rows, allsamewidth
                    PushButtonGroup.NORMAL  // Only one selected at a time
                    )
                    , RIGHT, SAME);

        pbPtType.setSelected(m_settings.getAdvFilterActive()?0:1);

        getSettings();
    }
    
    
    public void updateButtons() {
//        m_userOptionTimesLeft.setText(Convert.toString(m_GPSstate.userOptionTimesLeft));
//        m_edUpdateRate.setText(Convert.toString(m_GPSstate.dtUpdateRate));
//        m_edBaudRate.setText(Convert.toString(m_GPSstate.dtBaudRate));
//        m_edGLL_Period.setText(Convert.toString(m_GPSstate.dtGLL_Period));
//        m_edRMC_Period.setText(Convert.toString(m_GPSstate.dtRMC_Period));
//        m_edVTG_Period.setText(Convert.toString(m_GPSstate.dtVTG_Period));
//        m_edGSA_Period.setText(Convert.toString(m_GPSstate.dtGSA_Period));
//        m_edGSV_Period.setText(Convert.toString(m_GPSstate.dtGSV_Period));
//        m_edGGA_Period.setText(Convert.toString(m_GPSstate.dtGGA_Period));
//        m_edZDA_Period.setText(Convert.toString(m_GPSstate.dtZDA_Period));
//        m_edMCHN_Period.setText(Convert.toString(m_GPSstate.dtMCHN_Period));
//        
//        
//        m_userOptionTimesLeft.repaintNow();
//        m_edUpdateRate.repaintNow();
//        m_edBaudRate.repaintNow();
//        m_edGLL_Period.repaintNow();
//        m_edRMC_Period.repaintNow();
//        m_edVTG_Period.repaintNow();
//        m_edGSA_Period.repaintNow();
//        m_edGSV_Period.repaintNow();
//        m_edGGA_Period.repaintNow();
//        m_edZDA_Period.repaintNow();
//        m_edMCHN_Period.repaintNow();

    }
    
    private void setFilters() {
        for (int i = 0; i < m_gpsFilters.length; i++) {
            GPSFilterAdvanced filter = m_gpsFilters[i];
            filter.setMinRecCount(m_settings.getFilterMinRecCount());
            filter.setMaxRecCount(m_settings.getFilterMaxRecCount());
            filter.setMinSpeed(m_settings.getFilterMinSpeed());
            filter.setMaxSpeed(m_settings.getFilterMaxSpeed());
            filter.setMinDist(m_settings.getFilterMinDist());
            filter.setMaxDist(m_settings.getFilterMaxDist());
            filter.setMaxPDOP((int)(m_settings.getFilterMaxPDOP()*100));
            filter.setMaxHDOP((int)(m_settings.getFilterMaxHDOP()*100));
            filter.setMaxVDOP((int)(m_settings.getFilterMaxVDOP()*100));
            filter.setMinNSAT(m_settings.getFilterMinNSAT());
        }
    }

    
    public void setSettings() {

        m_settings.setFilterMinRecCount(Convert.toInt(m_minRecCount.getText()));
        m_settings.setFilterMaxRecCount(Convert.toInt(m_maxRecCount.getText()));
        m_settings.setFilterMinSpeed(Convert.toFloat(m_minSpeed.getText()));
        m_settings.setFilterMaxSpeed(Convert.toFloat(m_maxSpeed.getText()));
        m_settings.setFilterMinDist(Convert.toFloat(m_minDist.getText()));
        m_settings.setFilterMaxDist(Convert.toFloat(m_maxDist.getText()));
        m_settings.setFilterMaxPDOP((Convert.toFloat(m_maxPDOP.getText())));
        m_settings.setFilterMaxHDOP((Convert.toFloat(m_maxHDOP.getText())));
        m_settings.setFilterMaxVDOP((Convert.toFloat(m_maxVDOP.getText())));
        m_settings.setFilterMinNSAT(Convert.toInt(m_minNSAT.getText()));

        m_settings.saveSettings();
        setFilters();
    }

    public void getSettings() {
        for (int i = 0; i < m_gpsFilters.length; i++) {
            m_minRecCount.setText(Convert.toString(m_settings.getFilterMinRecCount()));
            m_maxRecCount.setText(Convert.toString(m_settings.getFilterMaxRecCount()));
            m_minSpeed.setText(Convert.toString(m_settings.getFilterMinSpeed(),2));
            m_maxSpeed.setText(Convert.toString(m_settings.getFilterMaxSpeed(),2));
            m_minDist.setText(Convert.toString(m_settings.getFilterMinDist(),2));
            m_maxDist.setText(Convert.toString(m_settings.getFilterMaxDist(),2));
            m_maxPDOP.setText(Convert.toString(m_settings.getFilterMaxPDOP(),2));
            m_maxHDOP.setText(Convert.toString(m_settings.getFilterMaxHDOP(),2));
            m_maxVDOP.setText(Convert.toString(m_settings.getFilterMaxVDOP(),2));
            m_minNSAT.setText(Convert.toString(m_settings.getFilterMinNSAT()));
        }
        setFilters();
    }
    
    public void clearSettings() {
        m_minRecCount.setText("0");
        m_maxRecCount.setText("0");
        m_minSpeed.setText("0");
        m_maxSpeed.setText("0");
        m_minDist.setText("0");
        m_maxDist.setText("0");
        m_maxPDOP.setText("0");
        m_maxHDOP.setText("0");
        m_maxVDOP.setText("0");
        m_minNSAT.setText("0");
        setSettings();
    }
    
    public void onEvent(Event event) {
        super.onEvent(event);
        switch (event.type) {
        case ControlEvent.PRESSED:
            event.consumed=true;
        if(event.target==m_btSet) {
            setSettings();
        } else if(event.target==m_btClear) {
             clearSettings();
        } else if (event.target==pbPtType) {
            m_settings.setAdvFilterActive(pbPtType.getSelected()==0);

        } else if (event.target == this) {
            //m_GPSstate.getFlashUserOption();
        } else {
            event.consumed=false;
        }
        break;
        case GpsEvent.DATA_UPDATE:
            if(event.target==this) {
                updateButtons();
                event.consumed=true;
            }
        }
    }
}
