/*
 * Created on 11 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.io;

import java.io.Reader;

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public final class FileJ2SE extends java.io.File {
    private String path;
    /**
     * @param path
     */
    public FileJ2SE(String path) {
        super(path);
//        super();
        this.path=path;
        //this.connect(path);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param path
     * @param mode
     */
    public FileJ2SE(String path, int mode) 
//        throws IOException 
    {
  //      super();
        super(path);
        this.path=path;
        //this.connect(path,mode);
    }

    /**
     * @param path
     * @param mode
     * @param slot
     */
    public FileJ2SE(String path, int mode, int slot) 
//            throws IOException 
    {
//        super();
        super(path);
        this.path=path;
        //this.connect(path,mode);
    }
    
//    public void delete() {
//        //TODO:
//        //com.sun.midp.io.j2me.storage.RandomAccessStream.File.delete(this.path);
//    }
//    public final static int READx = ;
//    public final static int READ_WRITE = java.io.Connector.READ_WRITE;
//    public final static int CREATE = java.Connector.READ_WRITE;
    //this.READ_WRITE_TRUNCATE;
}
