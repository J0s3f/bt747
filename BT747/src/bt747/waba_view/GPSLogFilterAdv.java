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
import waba.ui.ControlEvent;
import waba.ui.Edit;
import waba.ui.Event;
import waba.ui.Label;
import waba.ui.PushButtonGroup;

import gps.GpsEvent;
import gps.log.GPSFilterAdvanced;

import bt747.Txt;
import bt747.model.Controller;
import bt747.model.Model;
import bt747.sys.Convert;
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
    
    
    private Button m_btSet;
    private Button m_btClear;

    private PushButtonGroup pbPtType;
    private String[] C_PB_TYPE_NAMES= {
            Txt.ACTIVE,
            Txt.INACTIVE
            };
    
    private Model m;
    private Controller c;
    
    public GPSLogFilterAdv(Controller c, Model m) {;
        this.m=m;
        this.c=c;
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

        pbPtType.setSelected(m.getAdvFilterActive()?0:1);

        getSettings();
    }
       
    public void setSettings() {

        c.setFilterMinRecCount(Convert.toInt(m_minRecCount.getText()));
        c.setFilterMaxRecCount(Convert.toInt(m_maxRecCount.getText()));
        c.setFilterMinSpeed(Convert.toFloat(m_minSpeed.getText()));
        c.setFilterMaxSpeed(Convert.toFloat(m_maxSpeed.getText()));
        c.setFilterMinDist(Convert.toFloat(m_minDist.getText()));
        c.setFilterMaxDist(Convert.toFloat(m_maxDist.getText()));
        c.setFilterMaxPDOP((Convert.toFloat(m_maxPDOP.getText())));
        c.setFilterMaxHDOP((Convert.toFloat(m_maxHDOP.getText())));
        c.setFilterMaxVDOP((Convert.toFloat(m_maxVDOP.getText())));
        c.setFilterMinNSAT(Convert.toInt(m_minNSAT.getText()));

        c.saveSettings();
        c.setFilters();
    }

    public void getSettings() {
        m_minRecCount.setText(Convert.toString(m.getFilterMinRecCount()));
        m_maxRecCount.setText(Convert.toString(m.getFilterMaxRecCount()));
        m_minSpeed.setText(Convert.toString(m.getFilterMinSpeed(), 2));
        m_maxSpeed.setText(Convert.toString(m.getFilterMaxSpeed(), 2));
        m_minDist.setText(Convert.toString(m.getFilterMinDist(), 2));
        m_maxDist.setText(Convert.toString(m.getFilterMaxDist(), 2));
        m_maxPDOP.setText(Convert.toString(m.getFilterMaxPDOP(), 2));
        m_maxHDOP.setText(Convert.toString(m.getFilterMaxHDOP(), 2));
        m_maxVDOP.setText(Convert.toString(m.getFilterMaxVDOP(), 2));
        m_minNSAT.setText(Convert.toString(m.getFilterMinNSAT()));
        c.setFilters();
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
            c.setAdvFilterActive(pbPtType.getSelected()==0);

        } else if (event.target == this) {
            //m_GPSstate.getFlashUserOption();
        } else {
            event.consumed=false;
        }
        break;
        }
    }
}
