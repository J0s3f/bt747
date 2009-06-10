/**
 * 
 */
package net.sf.bt747.j4me.app;

import gps.BT747Constants;
import net.sf.bt747.j4me.app.screens.BT747Dialog;

import org.j4me.ui.components.Label;
import org.j4me.ui.components.RadioButton;

import bt747.model.Model;

/**
 * @author Mario
 * 
 */
public class ConnectConfig extends BT747Dialog {

    private boolean screenIsSetup = false;

    private RadioButton rbProtocol;

    private void setupScreen() {
        if (!screenIsSetup) {
            screenIsSetup = true;
            deleteAll();
            Label l;
            l = new Label("Select output format:");
            append(l);
            rbProtocol = new RadioButton();
            rbProtocol.append("Default MTK");
            rbProtocol.append("Holux M1000C / GR245");
            // rbProtocol.append("DPL700 /SIRFIII (not working)");
            int index;
            switch (c.getAppModel().getIntOpt(AppModel.DEVICE_PROTOCOL)) {
            default:
            case BT747Constants.PROTOCOL_MTK:
                index = 0;
                break;
            case BT747Constants.PROTOCOL_DPL700:
                index = 2;
                break;
            case BT747Constants.PROTOCOL_PHLX:
                index = 1;
                break;
            }
            rbProtocol.setSelectedIndex(index);
            append(rbProtocol);
        }
    }

    public void showNotify() {
        setupScreen();
    }

    protected void acceptNotify() {
        switch (rbProtocol.getSelectedIndex()) {
        case 0:
            c.setIntOpt(Model.DEVICE_PROTOCOL, BT747Constants.PROTOCOL_MTK);
            break;
        case 1:
            c.setIntOpt(Model.DEVICE_PROTOCOL, BT747Constants.PROTOCOL_PHLX);
            break;
        case 2:
            c
                    .setIntOpt(Model.DEVICE_PROTOCOL,
                            BT747Constants.PROTOCOL_DPL700);
            break;
        }
        deleteAll();
        next.show();
        super.acceptNotify();
    }
    
    protected void declineNotify() {
        deleteAll();
        previous.show();
        super.declineNotify();
    }


}
