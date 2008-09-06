/*
 * Created on 11 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.io;

import bt747.interfaces.Interface;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class BufFile extends bt747.io.File {
    public BufFile(String path) {
        file = Interface.tr.getBufFileInstance(path);
    }

    public BufFile(String path, int mode, int card) {
        file = Interface.tr.getBufFileInstance(path,mode,card);
    }

    public BufFile(String path, int mode)  {
        file = Interface.tr.getBufFileInstance(path,mode);
    }

}
