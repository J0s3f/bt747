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

import bt747.Txt;
import bt747.model.AppController;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.sys.Convert;

/**
 * @author Mario De Weerd
 */
public class GPSLogFilterAdv extends Container {

    private Edit minRecCount;
    private Edit maxRecCount;
    private Edit minSpeed;
    private Edit maxSpeed;
    private Edit minDist;
    private Edit maxDist;
    private Edit maxPDOP;
    private Edit maxHDOP;
    private Edit maxVDOP;
    private Edit minNSAT;

    private Button btSet;
    private Button btClear;

    private PushButtonGroup pbPtType;
    private String[] C_PB_TYPE_NAMES = { Txt.ACTIVE, Txt.INACTIVE };

    private Model m;
    private AppController c;

    public GPSLogFilterAdv(final AppController c, final Model m) {
        this.m = m;
        this.c = c;
    }

    protected final void onStart() {
        super.onStart();

        add(minRecCount = new Edit(), LEFT, SAME);
        add(new Label(Txt.FLTR_REC), CENTER, SAME);
        add(maxRecCount = new Edit(), RIGHT, SAME);

        add(minSpeed = new Edit(), LEFT, AFTER);
        add(new Label(Txt.FLTR_SPD), CENTER, SAME);
        add(maxSpeed = new Edit(), RIGHT, SAME);

        add(minDist = new Edit(), LEFT, AFTER);
        add(new Label(Txt.FLTR_DST), CENTER, SAME);
        add(maxDist = new Edit(), RIGHT, SAME);

        add(new Label(Txt.FLTR_PDOP), CENTER, AFTER);
        add(maxPDOP = new Edit(), RIGHT, SAME);

        add(new Label(Txt.FLTR_HDOP), CENTER, AFTER);
        add(maxHDOP = new Edit(), RIGHT, SAME);

        add(new Label(Txt.FLTR_VDOP), CENTER, AFTER);
        add(maxVDOP = new Edit(), RIGHT, SAME);

        add(minNSAT = new Edit(), LEFT, AFTER);
        add(new Label(Txt.FLTR_NSAT), CENTER, SAME);

        String allowedKeys;
        allowedKeys = Edit.numbersSet + "-";
        minRecCount.setValidChars(allowedKeys);
        maxRecCount.setValidChars(allowedKeys);
        minNSAT.setValidChars(allowedKeys);
        allowedKeys += ".";
        minSpeed.setValidChars(allowedKeys);
        maxSpeed.setValidChars(allowedKeys);
        minDist.setValidChars(allowedKeys);
        maxDist.setValidChars(allowedKeys);
        maxPDOP.setValidChars(allowedKeys);
        maxHDOP.setValidChars(allowedKeys);
        maxVDOP.setValidChars(allowedKeys);

        add(new Label(Txt.IGNORE_0VALUES), CENTER, AFTER);

        btSet = new Button(Txt.SET);
        add(btSet, LEFT, AFTER + 3); //$NON-NLS-1$
        btClear = new Button(Txt.CLEAR);
        add(btClear, AFTER, SAME); //$NON-NLS-1$

        add(pbPtType = new PushButtonGroup(C_PB_TYPE_NAMES, // labes for buttons
                true, // atleastone
                0, 1, 2, 1, true, // selected, gap, insidegap, rows,
                                    // allsamewidth
                PushButtonGroup.NORMAL // Only one selected at a time
        ), RIGHT, SAME);

        pbPtType.setSelected(m.getAdvFilterActive() ? 0 : 1);

        getSettings();
    }

    public final void setSettings() {

        c.setFilterMinRecCount(Convert.toInt(minRecCount.getText()));
        c.setFilterMaxRecCount(Convert.toInt(maxRecCount.getText()));
        c.setFilterMinSpeed(Convert.toFloat(minSpeed.getText()));
        c.setFilterMaxSpeed(Convert.toFloat(maxSpeed.getText()));
        c.setFilterMinDist(Convert.toFloat(minDist.getText()));
        c.setFilterMaxDist(Convert.toFloat(maxDist.getText()));
        c.setFilterMaxPDOP((Convert.toFloat(maxPDOP.getText())));
        c.setFilterMaxHDOP((Convert.toFloat(maxHDOP.getText())));
        c.setFilterMaxVDOP((Convert.toFloat(maxVDOP.getText())));
        c.setFilterMinNSAT(Convert.toInt(minNSAT.getText()));

        c.saveSettings();
        c.setFilters();
    }

    public final void getSettings() {
        minRecCount.setText(Convert.toString(m.getFilterMinRecCount()));
        maxRecCount.setText(Convert.toString(m.getFilterMaxRecCount()));
        minSpeed.setText(Convert.toString(m.getFilterMinSpeed(), 2));
        maxSpeed.setText(Convert.toString(m.getFilterMaxSpeed(), 2));
        minDist.setText(Convert.toString(m.getFilterMinDist(), 2));
        maxDist.setText(Convert.toString(m.getFilterMaxDist(), 2));
        maxPDOP.setText(Convert.toString(m.getFilterMaxPDOP(), 2));
        maxHDOP.setText(Convert.toString(m.getFilterMaxHDOP(), 2));
        maxVDOP.setText(Convert.toString(m.getFilterMaxVDOP(), 2));
        minNSAT.setText(Convert.toString(m.getFilterMinNSAT()));
        c.setFilters();
    }

    public final void clearSettings() {
        minRecCount.setText("0");
        maxRecCount.setText("0");
        minSpeed.setText("0");
        maxSpeed.setText("0");
        minDist.setText("0");
        maxDist.setText("0");
        maxPDOP.setText("0");
        maxHDOP.setText("0");
        maxVDOP.setText("0");
        minNSAT.setText("0");
        setSettings();
    }

    public final void onEvent(final Event event) {
        super.onEvent(event);
        switch (event.type) {
        case ControlEvent.PRESSED:
            event.consumed = true;
            if (event.target == btSet) {
                setSettings();
            } else if (event.target == btClear) {
                clearSettings();
            } else if (event.target == pbPtType) {
                c.setAdvFilterActive(pbPtType.getSelected() == 0);

            } else if (event.target == this) {
                // m_GPSstate.getFlashUserOption();
            } else {
                event.consumed = false;
            }
            break;
        default:
            break;
        }
    }
    
    public final void modelEvent(final ModelEvent event) {
        // Do nothing
    }
}
