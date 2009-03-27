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

import waba.sys.Settings;
import waba.ui.Button;
import waba.ui.ComboBox;
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Edit;
import waba.ui.Event;
import waba.ui.Label;
import waba.util.Vector;

import net.sf.bt747.waba.system.WabaFile;

import bt747.Txt;
import bt747.model.AppSettings;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.model.ModelListener;
import bt747.sys.Convert;
import bt747.sys.File;
import bt747.waba_view.ui.FileSelect;

/**
 * The purpose of this container is to configure file settings
 * 
 * @author Mario De Weerd
 */
public final class GPSLogFile extends Container implements ModelListener {

    private final Model m;
    private final AppController c;

    public GPSLogFile(final AppController c, final Model m) {
        this.c = c;
        this.m = m;
    }

    private Edit edBaseDirName;
    private Button btSelectBaseDirName;
    private Edit edLogFileName;
    private Button btSelectLogFileName;
    private Edit edReportBaseName;
    private Edit edChunkSize;
    private Edit edTimeout;
    private ComboBox cbVolumes;
    private ComboBox cblogReqAhead;

    private static final String[] C_LOG_REQ_AHEAD = { "0", "1", "2", "3", "4",
            "5" };

    private Button btChangeSettings;
    private Button btDefaultSettings;

    protected final void onStart() {
        Label tmp;
        int idx;

        // OUTPUT DIRECTORY AND "+" button
        add(tmp = new Label(Txt.getString(Txt.OUTPUT_DIR)), LEFT, AFTER); //$NON-NLS-1$
        add(btSelectBaseDirName = new Button("+"), RIGHT, SAME);
        edBaseDirName = new Edit("");
        add(edBaseDirName); //$NON-NLS-1$
        edBaseDirName.setRect(AFTER, SAME, FILL
                - btSelectBaseDirName.getSize().width(), PREFERRED, tmp);

        // RAW DATA FILE
        add(tmp = new Label(Txt.getString(Txt.LOGFILE)), LEFT, AFTER); //$NON-NLS-1$
        add(btSelectLogFileName = new Button("+"), RIGHT, SAME);
        edLogFileName = new Edit("");
        add(edLogFileName);
        edLogFileName.setRect(AFTER, SAME, FILL
                - btSelectLogFileName.getSize().width(), PREFERRED, tmp);

        add(new Label(Txt.getString(Txt.REPORT)), LEFT, AFTER); //$NON-NLS-1$
        add(edReportBaseName = new Edit(""), AFTER, SAME); //$NON-NLS-1$

        add(new Label(Txt.getString(Txt.CHUNK)), LEFT, AFTER); //$NON-NLS-1$
        add(edChunkSize = new Edit(""), AFTER, SAME); //$NON-NLS-1$
        edChunkSize.setValidChars(Edit.numbersSet);
        add(new Label(Txt.getString(Txt.CHUNK_AHEAD)), LEFT, AFTER);
        add(cblogReqAhead = new ComboBox(C_LOG_REQ_AHEAD), AFTER, SAME);
        idx = m.getIntOpt(AppSettings.LOGAHEAD);
        if (idx > cblogReqAhead.size() - 1) {
            idx = cblogReqAhead.size() - 1;
        }
        cblogReqAhead.select(idx);
        add(new Label(Txt.getString(Txt.READ_TIMEOUT)), LEFT, AFTER); //$NON-NLS-1$
        add(edTimeout = new Edit(""), AFTER, SAME); //$NON-NLS-1$
        edTimeout.setValidChars(Edit.numbersSet);

        // The view does a little bit of control
        // - left it here because it is platform dependent.
        if (Settings.platform.startsWith("Palm")) {
            Vector v = new Vector(50);
            int Card = m.getCard();
            for (int i = 0; i < 255; i++) {
                if (WabaFile.isCardInserted(i)) {
                    v.addElement("" + i);
                    if (Card == i) {
                        idx = v.size() - 1;
                    }
                }
            }
            v.addElement("-1");
            if (Card == -1) {
                idx = v.size() - 1;
            }
            add(new Label(Txt.getString(Txt.CARD_VOL)), LEFT, AFTER); //$NON-NLS-1$
            add(cbVolumes = new ComboBox((String[]) v.toObjectArray()), AFTER,
                    SAME);
            cbVolumes.select(idx);
        }

        btChangeSettings = new Button(Txt.getString(Txt.APPLY_SET));
        add(btChangeSettings, CENTER, AFTER + 5);
        btDefaultSettings = new Button(Txt.getString(Txt.DEFAULT_SET));
        add(btDefaultSettings, CENTER, AFTER + 5);

        updateValues();

    }

    private boolean setting = false;

    private final void updateValues() {
        if (!setting) {
            edBaseDirName.setText(m.getStringOpt(AppSettings.OUTPUTDIRPATH));
            edReportBaseName
                    .setText(m.getStringOpt(AppSettings.REPORTFILEBASE));
            edLogFileName.setText(m.getStringOpt(AppSettings.LOGFILERELPATH));
            edChunkSize.setText("" + m.getChunkSize());
            edTimeout.setText("" + m.getDownloadTimeOut());
        }
    }

    private final void updateFullPath() {
        c.setStringOpt(AppSettings.LOGFILEPATH, m
                .getStringOpt(AppSettings.OUTPUTDIRPATH)
                + File.separatorStr
                + m.getStringOpt(AppSettings.LOGFILERELPATH));
    }

    public final void onEvent(final Event event) {
        switch (event.type) {
        case ControlEvent.PRESSED:
            if (event.target == btChangeSettings) {
                setting = true;
                c.setStringOpt(AppSettings.OUTPUTDIRPATH, edBaseDirName
                        .getText());
                c.setStringOpt(AppSettings.LOGFILERELPATH, edLogFileName
                        .getText());
                c.setOutputFileRelPath(edReportBaseName.getText());
                updateFullPath();
                c.setChunkSize(Convert.toInt(edChunkSize.getText()));
                c.setDownloadTimeOut(Convert.toInt(edTimeout.getText()));
                if (Settings.platform.startsWith("Palm")) {
                    c.setCard(Convert.toInt((String) cbVolumes
                            .getSelectedItem()));
                }
                c.setLogRequestAhead(Convert.toInt((String) cblogReqAhead
                        .getSelectedItem()));
                setting = false;
                updateValues();
                c.saveSettings(); // Explicitally save settings
            } else if (event.target == btSelectBaseDirName) {
                FileSelect fs = new FileSelect();
                fs.setDirOnly(true);
                fs.setPath(edBaseDirName.getText());
                if (Settings.platform.startsWith("Palm")) {
                    fs.setCardSlot(Convert.toInt((String) cbVolumes
                            .getSelectedItem()));
                }
                fs.popupBlockingModal();
                // m_edBaseDirName.setText(fs.getPath());
                c.setStringOpt(AppSettings.OUTPUTDIRPATH, fs.getPath());
                updateFullPath();
            } else if (event.target == btSelectLogFileName) {
                FileSelect fs = new FileSelect();
                fs.setRoot(edBaseDirName.getText());
                fs.setPath(edLogFileName.getText());
                // fs.setDirOnly(false); //Default
                if (Settings.platform.startsWith("Palm")) {
                    fs.setCardSlot(Convert.toInt((String) cbVolumes
                            .getSelectedItem()));
                }
                fs.popupBlockingModal();
                // m_edLogFileName.setText(fs.getRelPath());
                c.setStringOpt(AppSettings.LOGFILERELPATH, fs.getRelPath());
                updateFullPath();
            } else if (event.target == btDefaultSettings) {
                m.defaultSettings();
                updateValues();
            }
            break;
        case ControlEvent.FOCUS_OUT:
            if (event.target == edLogFileName) {
                c.setStringOpt(AppSettings.LOGFILERELPATH, edLogFileName
                        .getText());
                updateFullPath();
            } else if (event.target == edBaseDirName) {
                c.setStringOpt(AppSettings.OUTPUTDIRPATH, edBaseDirName
                        .getText());
                updateFullPath();
            } else if (event.target == edReportBaseName) {
                c.setOutputFileRelPath(edReportBaseName.getText());
            }
            break;
        default:
        }
    }

    public final void modelEvent(final ModelEvent event) {
        // switch (event.getType()) {
        // case ModelEvent.LOGFILEPATH_UPDATE:
        // case ModelEvent.OUTPUTFILEPATH_UPDATE:
        // case ModelEvent.WORKDIRPATH_UPDATE:
        // updateValues();
        // break;
        // }
    }
}
