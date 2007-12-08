package ui;
import waba.fx.Rect;
import waba.io.File;
import waba.ui.ControlEvent;
import waba.ui.Event;
import waba.ui.ListBox;
import waba.ui.Window;
import waba.util.Vector;
/*
 * Created on 15 sept. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FileSelect extends Window {
    
    private String root="";
    private String relPath="";
    private int cardSlot = -1;
    private ListBox cb;
    private int defaultIdx;
    private boolean dirOnly=false;

    /**
     * 
     */
    public FileSelect() {
        this("Select path");
    }

    public FileSelect(String title) {
        this(title, RECT_BORDER);
    }

    /**
     * @param title
     * @param borderStyle
     */
    public FileSelect(String title, byte borderStyle) {
        super(title, borderStyle);
        root="";
        relPath="/";
    }
    
    public void setDirOnly(final boolean b) {
        dirOnly=b;
    }
    
    public void setPath(final String path) {
        if(dirOnly&&!path.endsWith("/")) {
            relPath=path+"/";
        } else {
            relPath=path;
        }
    }

    public void setRoot(final String path) {
        if(path.endsWith("/")) {
            root=path;
        } else {
            root=path+"/";
        }
    }
    public String getPath() {
        if(relPath.endsWith("/")) {
            return root+relPath.substring(0,relPath.length()-1);
        } else {
            return root+relPath;
        }
    }

    public String getRelPath() {
        if(relPath.endsWith("/")) {
            return relPath.substring(0,relPath.length()-1);
        } else {
            return relPath;
        }
    }
    
    public void setCardSlot(int card) {
        cardSlot=card;
    }
    
    /* (non-Javadoc)
     * @see waba.ui.Window#onPopup()
     */
    protected void onPopup() {
        buildWindow();
    }
    
    private void buildWindow() {
        Rect r=new Rect();
        //r.set(getParentWindow().getClientRect());
        //setRect(r.modifiedBy(2, 2, -4, -4)); // same gap in all corners
        filePopList(root,relPath);
        add(cb);
        r=getAbsoluteRect().modifiedBy(4,26,-8,-38);
        //r.height=cb.getPreferredHeight();
        cb.setRect(r);
        cb.select(defaultIdx);
        cb.enableHorizScroll();
        //add(cb);
        //cb.get
     //   cb.setRect(getParentWindow().getAbsoluteRect().modifiedBy(4, 4, -8, -8));
    }
    
    
    private final int buildFileList(
            final String root,
            final String path,
            Vector v,
            final int depth,
            final int maxitems) {
       int added=0;
       if (path == null||(depth <=0)) return added;
       File file = new File(root+path,File.DONT_OPEN,cardSlot);
       String []list = file.listFiles();
        if (list != null) {
            for (int i =0; i < list.length; i++) {
                if (list[i] != null) {
                    if (!dirOnly
                        || list[i].endsWith("/")) {// is a path?
                        v.addElement(path+list[i]);
                        added++;
                    }
                }
            }
            if(added<maxitems) {
                for (int i =0; i < list.length; i++) {
                    if (list[i].endsWith("/")&&(depth>1)&&(added<maxitems)) {// is a path?
                        added+=buildFileList(root,path+list[i],v,depth-1,maxitems-added);
                    }
                }
            }
        }
        return added;
    }

    private void filePopList(
            final String root,
            final String path) {
        Vector v = new Vector(50);
        String basePath="";
        int firstFoundIndex=1;
        if(!dirOnly) {
            v.add("../");
        }
        if(path.length()>0) {
            int offset;
            offset=path.lastIndexOf('/',path.length()-2);
            if(offset>=0) {
                v.add(basePath=path.substring(0, offset+1));
                firstFoundIndex++;
            } else {
                if (!dirOnly) {
                    v.add("./");
                }
            }
        }
        v.add(path);
        File file = new File(root+path,File.DONT_OPEN,cardSlot);
        if((!dirOnly)&&file.isDir()) {
            basePath=path+"/";
        }
        
        int added=buildFileList(root,dirOnly?relPath:basePath,v,2,15);
        String []files = (String[])v.toObjectArray();
        if (added!=0 && files[firstFoundIndex].charAt(1) == '[') { // is it a volume label?
            files[firstFoundIndex] = files[firstFoundIndex].substring(1); // remove the preceding slash
        }

        if(cb!=null) {
            remove(cb);
        }
        cb=new ListBox(files);
        defaultIdx=firstFoundIndex-1;
    }
    
    /* (non-Javadoc)
     * @see waba.ui.Control#onEvent(waba.ui.Event)
     */
    public void onEvent(Event event) {
        switch (event.type) {
        case ControlEvent.PRESSED:
            if(event.target==cb) {
                relPath=(String)cb.getSelectedItem();
                unpop();
            }
        }
    }
    
    
}
