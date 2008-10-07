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

/**
 * @author Mario De Weerd
 * 
 * Implements Holux Specific operations
 */
public final class GPSHoluxSpecific extends Container implements ModelListener {

    private final Model m;
    private final AppController c;

    private Label lbHoluxName;
    private Edit edHoluxName;
    private Label lbBTMacAddr;
    private Edit edHoluxBT_MacAddr;

    private Button btSet;
    private Button btMacSet;

    /**
     * 
     */
    public GPSHoluxSpecific(final Model m, final AppController c) {
        this.m = m;
        this.c = c;
    }

    /*
     * (non-Javadoc)
     * 
     * @see waba.ui.Container#onStart()
     */
    protected  final void onStart() {
        lbHoluxName = new Label(Txt.HOLUX_NAME);
        add(lbHoluxName, LEFT, TOP);
        add(edHoluxName = new Edit(""), AFTER, SAME);
        add(btSet = new Button(Txt.SET), CENTER, AFTER + 5);
        lbBTMacAddr = new Label(Txt.BT_MAC_ADDR);
        add(lbBTMacAddr, LEFT, AFTER + 5);
        add(edHoluxBT_MacAddr = new Edit(""), AFTER, SAME);
        add(btMacSet = new Button(Txt.SET), CENTER, AFTER + 5);
    }

    private void doSet() {
        c.setHoluxName(edHoluxName.getText().replace(',', ';'));
    }

    /**
     * Sets the MAC address for bluetooth (for devices that support it).
     */
    private void doSetBTAddr() {
        c.setBTMacAddr(edHoluxBT_MacAddr.getText());
    }

    private void updateData() {
        edHoluxName.setText(m.getHoluxName());
        edHoluxBT_MacAddr.setText(m.getBTAddr());
    }

    /**
     * Handle events for this object.
     * 
     * @param event
     *            The event to be interpreted.
     */
    public final void onEvent(final Event event) {
        switch (event.type) {
        case ControlEvent.PRESSED:
            if (event.target == this) {
                c.reqHoluxName();
                c.reqBTAddr();
                event.consumed = true;
            } else if (event.target == btSet) {
                doSet();
            } else if (event.target == btMacSet) {
                doSetBTAddr();
            }
            break;
        default:
        }
    }

    public final void modelEvent(final ModelEvent event) {
        switch (event.getType()) {
        case ModelEvent.UPDATE_HOLUX_NAME:
        case ModelEvent.DATA_UPDATE:
            updateData();
            break;
        }
    }
}
