/*
 * Created on 11 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.io;

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BufFile extends bt747.io.BufferedFile {
    public static final char separatorChar='/';
    /**
     * @param path
     */
    public BufFile(String path) {
        super(path);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param path
     * @param mode
     */
    public BufFile(String path, int mode) {
        super(path, mode);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param path
     * @param mode
     * @param slot
     */
    public BufFile(String path, int mode, int slot) {
        super(path, mode, slot);
        // TODO Auto-generated constructor stub
    }
    
    public static String getCardVolumePath() {
        if(getCardVolume()!=null) {
            return getCardVolume().getPath();
        } else {
            return null;
        }
    }
    
}
