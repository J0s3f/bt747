/*
 * Created on 22 juin 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gps;

import waba.io.File;

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BT747LogConvert {
    File m_File=null; 
    

    public void ToCSV(final String fileName) {
        if(File.isAvailable()) {
            m_File=new File(fileName,File.READ_ONLY);
            if(!m_File.isOpen()) {
                m_File=null;
            } else {
                
            }
        }
    }
    


}
