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

    private Button m_btChangeSettings;
    private Button m_btDefaultSettings;
    
    protected void onStart() {
        add(new Label("Output dir:"), LEFT, AFTER); //$NON-NLS-1$
        add(m_edBaseDirName = new Edit(""), AFTER, SAME); //$NON-NLS-1$
        add(new Label("LogFile:"), LEFT, AFTER); //$NON-NLS-1$
        add(m_edLogFileName = new Edit(""), AFTER, SAME); //$NON-NLS-1$
        add(new Label("Report :"), LEFT, AFTER); //$NON-NLS-1$
        add(m_edReportBaseName = new Edit(""), AFTER, SAME); //$NON-NLS-1$

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
    }

    public void onEvent( Event event ) {
        switch (event.type) {
        case ControlEvent.PRESSED:
            if (event.target==m_btChangeSettings) {
                m_settings.setBaseDirPath(m_edBaseDirName.getText());
                m_settings.setLogFile(m_edLogFileName.getText());
                m_settings.setReportFileBase(m_edReportBaseName.getText());
            } else if (event.target==m_btDefaultSettings) {
                m_settings.defaultSettings();
                updateValues();
            }
        break;
        }
    }

}
