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
import ui.FileSelect;

import waba.sys.Settings;
import waba.ui.ComboBox;
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Edit;
import waba.ui.Event;
import waba.ui.Label;
import waba.util.Vector;

import bt747.Txt;
import bt747.io.File;
import bt747.model.Model;
import bt747.model.ModelEvent;
import bt747.sys.Convert;
import bt747.ui.Button;

/** The purpose of this container is to configure file settings
 * 
 * @author Mario De Weerd
 */
public class GPSLogFile extends Container {
    
    Model m_settings;
    
    public GPSLogFile(Model settings) {
        m_settings=settings;
    }
    
    Edit m_edBaseDirName;
    Button m_btSelectBaseDirName;
    Edit m_edLogFileName;
    Button m_btSelectLogFileName;
    Edit m_edReportBaseName;
    Edit m_edChunkSize;
    Edit m_edTimeout;
    ComboBox m_cbVolumes;
    ComboBox m_cblogReqAhead;
    private static final String []C_LOG_REQ_AHEAD= {"0","1","2","3","4","5"};
    

    private Button m_btChangeSettings;
    private Button m_btDefaultSettings;
    
    protected void onStart() {
        Label tmp;
        int idx;
        
        // OUTPUT DIRECTORY AND "+" button
        add(tmp=new Label(Txt.OUTPUT_DIR), LEFT, AFTER); //$NON-NLS-1$
        add(m_btSelectBaseDirName=new Button("+"), RIGHT, SAME);
        m_edBaseDirName = new Edit("");
        add(m_edBaseDirName); //$NON-NLS-1$
        m_edBaseDirName.setRect(AFTER, SAME,FILL-m_btSelectBaseDirName.getSize().width(),PREFERRED,tmp);
        
        // RAW DATA FILE
        add(tmp=new Label(Txt.LOGFILE), LEFT, AFTER); //$NON-NLS-1$
        add(m_btSelectLogFileName=new Button("+"), RIGHT, SAME);
        m_edLogFileName = new Edit("");
        add(m_edLogFileName);
        m_edLogFileName.setRect(AFTER, SAME,FILL-m_btSelectLogFileName.getSize().width(),PREFERRED,tmp);
        
        add(new Label(Txt.REPORT), LEFT, AFTER); //$NON-NLS-1$
        add(m_edReportBaseName = new Edit(""), AFTER, SAME); //$NON-NLS-1$

        add(new Label(Txt.CHUNK), LEFT, AFTER); //$NON-NLS-1$
        add(m_edChunkSize = new Edit(""), AFTER, SAME); //$NON-NLS-1$
        m_edChunkSize.setValidChars(Edit.numbersSet);
        add(new Label(Txt.CHUNK_AHEAD), LEFT, AFTER);
        add(m_cblogReqAhead=new ComboBox(C_LOG_REQ_AHEAD),AFTER,SAME);
        idx=m_settings.getLogRequestAhead();
        if(idx>m_cblogReqAhead.size()-1) {
            idx=m_cblogReqAhead.size()-1;
        }
        m_cblogReqAhead.select(idx);
        add(new Label(Txt.READ_TIMEOUT), LEFT, AFTER); //$NON-NLS-1$
        add(m_edTimeout = new Edit(""), AFTER, SAME); //$NON-NLS-1$
        m_edTimeout.setValidChars(Edit.numbersSet);

        if(Settings.platform.startsWith("Palm")) {
            Vector v = new Vector(50);
            int Card=m_settings.getCard();
            for (int i = 0; i < 255; i++) {
                if (File.isCardInserted(i)) {
                    v.add(""+i);
                    if(Card==i) {
                        idx=v.size()-1;
                    }
                }
            }
            v.add("-1");
            if(Card==-1) {
                idx=v.size()-1;
            }
            add(new Label(Txt.CARD_VOL), LEFT, AFTER); //$NON-NLS-1$
            add( m_cbVolumes= new ComboBox((String[])v.toObjectArray()), AFTER,SAME);
            m_cbVolumes.select(idx);
        }
        

        m_btChangeSettings=new Button(Txt.APPLY_SET);
        add(m_btChangeSettings,CENTER,AFTER+5);
        m_btDefaultSettings=new Button(Txt.DEFAULT_SET);
        add(m_btDefaultSettings,CENTER,AFTER+5);

        updateValues();

    }
    
    private void updateValues() {
        m_edBaseDirName.setText(m_settings.getBaseDirPath());
        m_edReportBaseName.setText(m_settings.getReportFileBase());
        m_edLogFileName.setText(m_settings.getLogFile());
        m_edChunkSize.setText(Convert.toString(m_settings.getChunkSize()));
        m_edTimeout.setText(Convert.toString(m_settings.getDownloadTimeOut()));
    }

    public void onEvent( Event event ) {
        switch (event.type) {
        case ControlEvent.PRESSED:
            if (event.target==m_btChangeSettings) {
                m_settings.setBaseDirPath(m_edBaseDirName.getText());
                m_settings.setLogFile(m_edLogFileName.getText());
                m_settings.setReportFileBase(m_edReportBaseName.getText());
                m_settings.setChunkSize(Convert.toInt(m_edChunkSize.getText()));
                m_settings.setDownloadTimeOut(Convert.toInt(m_edTimeout.getText()));
                if(Settings.platform.startsWith("Palm")) {
                    m_settings.setCard(Convert.toInt((String)m_cbVolumes.getSelectedItem()));
                }
                m_settings.setLogRequestAhead(Convert.toInt((String)m_cblogReqAhead.getSelectedItem()));
                m_settings.saveSettings(); // Explicitally save settings
            } else if (event.target==m_btSelectBaseDirName) {
                FileSelect fs=new FileSelect();
                fs.setDirOnly(true);
                fs.setPath(m_edBaseDirName.getText());
                if(Settings.platform.startsWith("Palm")) {
                    fs.setCardSlot(Convert.toInt((String)m_cbVolumes.getSelectedItem()));
                }
                fs.popupBlockingModal();
                m_edBaseDirName.setText(fs.getPath());
            } else if (event.target==m_btSelectLogFileName) {
                FileSelect fs=new FileSelect();
                fs.setRoot(m_edBaseDirName.getText());
                fs.setPath(m_edLogFileName.getText());
                //fs.setDirOnly(false);   //Default
                if(Settings.platform.startsWith("Palm")) {
                    fs.setCardSlot(Convert.toInt((String)m_cbVolumes.getSelectedItem()));
                }
                fs.popupBlockingModal();
                m_edLogFileName.setText(fs.getRelPath());
            } else if (event.target==m_btDefaultSettings) {
                m_settings.defaultSettings();
                updateValues();
            }
        break;
        default:
            if (event.type==ModelEvent.LOGFILEPATH_UPDATE
                ||event.type==ModelEvent.OUTPUTFILEPATH_UPDATE
                ||event.type==ModelEvent.WORKDIRPATH_UPDATE) {
                updateValues();
            }
        break;
        }
    }

}
