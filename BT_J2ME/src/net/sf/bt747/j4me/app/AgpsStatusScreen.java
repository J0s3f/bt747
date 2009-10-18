package net.sf.bt747.j4me.app;

import gps.log.out.CommonOut;
import gps.mvc.MtkModel;
import net.sf.bt747.j4me.app.screens.BT747Dialog;

import org.j4me.ui.components.Label;

import bt747.model.ModelEvent;
import bt747.model.ModelListener;
import bt747.sys.interfaces.BT747Int;

public final class AgpsStatusScreen extends BT747Dialog implements
        ModelListener {
    private Label txtAgpsInfo1;
    private Label txtAgpsInfo2;

    private boolean screenIsSetup = false;

    private void setupScreen() {
        if (!screenIsSetup) {
            screenIsSetup = true;
            deleteAll();
            setTitle("AGPS");
            setMenuText(null, "OK");

            append(new Label("AGPS data status (if available):"));
            txtAgpsInfo1 = new Label(null);
            txtAgpsInfo2 = new Label(null);
            append(txtAgpsInfo1);
            append(txtAgpsInfo2);
            updateAgps();
            invalidate();
        }
    }

    private final AppModel m() {
        return c.getAppModel();
    }

    public void showNotify() {
        setupScreen();
        updateAgps();
        m().addListener(this);
        c.setMtkDataNeeded(MtkModel.DATA_AGPS_STORED_RANGE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.j4me.ui.Dialog#hideNotify()
     */
    public void hideNotify() {
        m().removeListener(this);
    }

    public void modelEvent(final ModelEvent e) {
        // TODO Auto-generated method stub
        final int type = e.getType();
        switch (type) {
        case ModelEvent.DATA_UPDATE:
            switch (((BT747Int) e.getArg()).getValue()) {
            case MtkModel.DATA_AGPS_STORED_RANGE:
                updateAgps();
                break;
            }
            break;
        }
    }

    private final void updateAgps() {
        MtkModel mtk = m().mtkModel();
        if (mtk != null && mtk.hasAgps()) {
            final String[] a = { String.valueOf(mtk.getAgpsDataCount()),
                    CommonOut.getDateTimeStr(mtk.getAgpsStartTime()),
                    CommonOut.getDateTimeStr(mtk.getAgpsEndTime()) };
            final String[] b = {
                    CommonOut.getDateTimeStr(mtk.getAgpsStart2Time()),
                    CommonOut.getDateTimeStr(mtk.getAgpsEnd2Time()) };
            final String text1 = "AGPS Blocks: " + a[0] + " from " + a[1]
                    + " to " + a[2] + ".";
            final String text2 = "Other AGPS data: " + b[0] + ", " + b[1];
            txtAgpsInfo1.setLabel(text1);
            txtAgpsInfo2.setLabel(text2);
        } else {
            txtAgpsInfo1.setLabel("No AGPS info.");
            txtAgpsInfo2.setLabel(null);
        }
        invalidate();
    }

    protected void acceptNotify() {
        previous.show();
    }

    protected void declineNotify() {
        c.setMtkDataNeeded(MtkModel.DATA_AGPS_STORED_RANGE);
    }
}
