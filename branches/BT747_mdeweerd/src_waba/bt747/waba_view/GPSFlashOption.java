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

import bt747.model.ModelEvent;

import bt747.Txt;
import bt747.model.Model;
import bt747.model.ModelListener;
import bt747.sys.Convert;
import bt747.waba_view.ui.BT747MessageBox;

/**
 * @author Mario De Weerd
 */
public final class GPSFlashOption extends Container implements ModelListener {
    private final AppController c;
    private final Model m;

    private Edit userOptionTimesLeft;
    private Edit edUpdateRate;
    private Edit edBaudRate;
    private Edit edGLL_Period;
    private Edit edRMC_Period;
    private Edit edVTG_Period;
    private Edit edGSA_Period;
    private Edit edGSV_Period;
    private Edit edGGA_Period;
    private Edit edZDA_Period;
    private Edit edMCHN_Period;

    private Button btSet;

    public GPSFlashOption(final Model m, final AppController c) {
        this.m = m;
        this.c = c;
    }

    protected final void onStart() {
        super.onStart();

        add(new Label(Txt.TIMESLEFT), LEFT, TOP);
        add(userOptionTimesLeft = new Edit(), AFTER, SAME);
        userOptionTimesLeft.setEnabled(false);
        add(new Label(Txt.UPDATERATE), LEFT, AFTER);
        add(edUpdateRate = new Edit(), AFTER, SAME);
        add(new Label(Txt.BAUDRATE), LEFT, AFTER);
        add(edBaudRate = new Edit(), AFTER, SAME);
        edBaudRate.setEditable(false); // To protect the user
        edBaudRate.setEnabled(false);
        add(new Label("GLL " + Txt.PERIOD_ABBREV), LEFT, AFTER);
        add(edGLL_Period = new Edit(), AFTER, SAME);
        add(new Label("RMC " + Txt.PERIOD_ABBREV), AFTER, SAME);
        add(edRMC_Period = new Edit(), AFTER, SAME);
        add(new Label("VTG " + Txt.PERIOD_ABBREV), LEFT, AFTER);
        add(edVTG_Period = new Edit(), AFTER, SAME);
        add(new Label("GSA " + Txt.PERIOD_ABBREV), AFTER, SAME);
        add(edGSA_Period = new Edit(), AFTER, SAME);
        add(new Label("GSV " + Txt.PERIOD_ABBREV), LEFT, AFTER);
        add(edGSV_Period = new Edit(), AFTER, SAME);
        add(new Label("GGA " + Txt.PERIOD_ABBREV), AFTER, SAME);
        add(edGGA_Period = new Edit(), AFTER, SAME);
        add(new Label("ZDA " + Txt.PERIOD_ABBREV), LEFT, AFTER);
        add(edZDA_Period = new Edit(), AFTER, SAME);
        add(new Label("MCHN " + Txt.PERIOD_ABBREV), AFTER, SAME);
        add(edMCHN_Period = new Edit(), AFTER, SAME);

        btSet = new Button(Txt.SET);
        add(btSet, CENTER, AFTER + 3); //$NON-NLS-1$

    }

    private final void updateButtons() {
        userOptionTimesLeft.setText(Convert.toString(m
                .getDtUserOptionTimesLeft()));
        edUpdateRate.setText(Convert.toString(m.getDtUpdateRate()));
        edBaudRate.setText(Convert.toString(m.getDtBaudRate()));
        edGLL_Period.setText(Convert.toString(m.getDtGLL_Period()));
        edRMC_Period.setText(Convert.toString(m.getDtRMC_Period()));
        edVTG_Period.setText(Convert.toString(m.getDtVTG_Period()));
        edGSA_Period.setText(Convert.toString(m.getDtGSA_Period()));
        edGSV_Period.setText(Convert.toString(m.getDtGSV_Period()));
        edGGA_Period.setText(Convert.toString(m.getDtGGA_Period()));
        edZDA_Period.setText(Convert.toString(m.getDtZDA_Period()));
        edMCHN_Period.setText(Convert.toString(m.getDtMCHN_Period()));

        // m_userOptionTimesLeft.repaintNow();
        // m_edUpdateRate.repaintNow();
        // m_edBaudRate.repaintNow();
        // m_edGLL_Period.repaintNow();
        // m_edRMC_Period.repaintNow();
        // m_edVTG_Period.repaintNow();
        // m_edGSA_Period.repaintNow();
        // m_edGSV_Period.repaintNow();
        // m_edGGA_Period.repaintNow();
        // m_edZDA_Period.repaintNow();
        // m_edMCHN_Period.repaintNow();

    }

    private void setSettings() {
        BT747MessageBox mb;
        String[] mbStr = { Txt.WRITEFLASH, Txt.ABORT };
        mb = new BT747MessageBox(Txt.TITLE_ATTENTION,
                Txt.TXT_FLASH_LIMITED_WRITES, mbStr);
        mb.popupBlockingModal();
        if (mb.getPressedButtonIndex() == 0) {
            c.setFlashUserOption(false, // lock
                    Convert.toInt(edUpdateRate.getText()), Convert
                            .toInt(edBaudRate.getText()), Convert
                            .toInt(edGLL_Period.getText()), Convert
                            .toInt(edRMC_Period.getText()), Convert
                            .toInt(edVTG_Period.getText()), Convert
                            .toInt(edGSA_Period.getText()), Convert
                            .toInt(edGSV_Period.getText()), Convert
                            .toInt(edGGA_Period.getText()), Convert
                            .toInt(edZDA_Period.getText()), Convert
                            .toInt(edMCHN_Period.getText()));
        }

    }

    public final void onEvent(final Event event) {
        super.onEvent(event);
        switch (event.type) {
        case ControlEvent.PRESSED:
            event.consumed = true;
            if (event.target == btSet) {
                setSettings();
            } else if (event.target == this) {
                c.reqFlashUserOption();
            } else {
                event.consumed = false;
            }
            break;
        default:
        }
    }

    public final void modelEvent(final ModelEvent event) {
        if (event.getType() == ModelEvent.UPDATE_FLASH_CONFIG) {
            updateButtons();
        }
    }
}
