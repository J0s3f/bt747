package net.sf.bt747.j4me.app;

import org.j4me.ui.DeviceScreen;
import org.j4me.ui.Dialog;

public class BasePathSelectionScreen extends Dialog {

    private FileUsage mFileUsage;
    
    public BasePathSelectionScreen(AppController c, DeviceScreen previous) {
        mFileUsage = new FileManager();
    }
    
    public void showNotify() {
        // First selectroot
        
        
        // Then potentially select path in selected root.
    }
}
