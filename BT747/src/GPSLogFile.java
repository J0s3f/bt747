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
import waba.io.File;
import waba.sys.Convert;
import waba.sys.Settings;
import waba.ui.Button;
import waba.ui.ComboBox;
import waba.ui.Container;
import waba.ui.ControlEvent;
import waba.ui.Edit;
import waba.ui.Event;
import waba.ui.Label;
import waba.util.Vector;

/** The purpose of this container is to configure file settings
 * 
 * @author Mario De Weerd
 */
public class GPSLogFile extends Container {
    
    AppSettings m_settings;
    
    GPSLogFile(AppSettings settings) {
        m_settings=settings;
    }
    
    Edit m_edBaseDirName;
    Edit m_edLogFileName;
    Edit m_edReportBaseName;
    Edit m_edChunkSize;
    Edit m_edTimeout;
    ComboBox m_cbVolumes;

    private Button m_btChangeSettings;
    private Button m_btDefaultSettings;
    
    protected void onStart() {
        add(new Label("Output dir:"), LEFT, AFTER); //$NON-NLS-1$
        add(m_edBaseDirName = new Edit(""), AFTER, SAME); //$NON-NLS-1$
        add(new Label("LogFile:"), LEFT, AFTER); //$NON-NLS-1$
        add(m_edLogFileName = new Edit(""), AFTER, SAME); //$NON-NLS-1$
        add(new Label("Report :"), LEFT, AFTER); //$NON-NLS-1$
        add(m_edReportBaseName = new Edit(""), AFTER, SAME); //$NON-NLS-1$

        add(new Label("Chunk :"), LEFT, AFTER); //$NON-NLS-1$
        add(m_edChunkSize = new Edit(""), AFTER, SAME); //$NON-NLS-1$
        m_edChunkSize.setValidChars(Edit.numbersSet);
        add(new Label("Read timeout (ms) :"), LEFT, AFTER); //$NON-NLS-1$
        add(m_edTimeout = new Edit(""), AFTER, SAME); //$NON-NLS-1$
        m_edTimeout.setValidChars(Edit.numbersSet);

        if(Settings.platform.startsWith("Palm")) {
            Vector v = new Vector(50);
            int Card=m_settings.getCard();
            int idx=0;
            for (int i = 0; i < 255; i++) {
                if (File.isCardInserted(i)) {
                    v.add(""+i);
                    if(Card==i) {
                        idx=v.size()-1;
                    }
                }
            }
            add(new Label("Card/Volume:"), LEFT, AFTER); //$NON-NLS-1$
            add( m_cbVolumes= new ComboBox((String[])v.toObjectArray()), AFTER,SAME);
            m_cbVolumes.select(idx);
        }

        m_btChangeSettings=new Button("Set values");
        add(m_btChangeSettings,CENTER,AFTER+5);
        m_btDefaultSettings=new Button("Default settings");
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
                // TODO: Do this in settings itself.
                m_settings.saveSettings();
            } else if (event.target==m_btDefaultSettings) {
                m_settings.defaultSettings();
                updateValues();
            }
        break;
        }
    }

}
