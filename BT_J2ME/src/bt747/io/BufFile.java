/*
 * Created on 11 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.io;

import java.io.IOException;

/**
 * @author Mario De Weerd
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class BufFile extends bt747.io.File {
    public BufFile(String path) {
        super(path);
    }

    public BufFile(String path, int mode, int card) throws IOException {
        super(path, mode, card);
    }

    public BufFile(String path, int mode) throws IOException {
        super(path, mode);
    }
}
