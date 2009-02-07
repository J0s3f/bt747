/**
 * 
 */
package net.sf.bt747.j2se.app.list;

import gps.log.out.CommonOut;

import java.awt.Component;
import java.awt.Dimension;
import java.io.FileInputStream;
import java.lang.ref.SoftReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;

import org.jdesktop.swingx.graphics.GraphicsUtilities;

import bt747.j2se_view.model.FileWaypoint;

/**
 * @author Mario
 * 
 * TODO MUST BE COMPLETELY REWRITTEN TO HANDLE NON-IMAGE FILES.
 * 
 */
@SuppressWarnings("serial")
public final class FileListCellRenderer implements WaypointListCellComponent {

    private static final java.util.HashMap<String, SoftReference<ImageListPanel>> panels = new java.util.HashMap<String, SoftReference<ImageListPanel>>();

    private static ExecutorService loader = Executors
            .newCachedThreadPool(new ThreadFactory() {
                private int count = 0;

                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "loader-" + count++);
                    t.setDaemon(true);
                    return t;
                }
            });
    private final Semaphore availThreads = new Semaphore(4);

    public Component getListCellRendererComponent(final JList list,
            final Object value, final int index, final boolean isSelected,
            final boolean cellHasFocus) {
        String path;
        final FileWaypoint v = (FileWaypoint) value;
        path = v.getPath();
        SoftReference<ImageListPanel> s;
        synchronized (panels) {
            s = panels.get(path);
        }
        ImageListPanel pn = null;
        if (s != null) {
            pn = s.get();
        }

        if (pn == null) {
            pn = new ImageListPanel();
            pn.setLabel(getText(v));
            loader.execute(new IconLoader(pn, path, list, index));
        }
        pn.setOpaque(false);

        if (isSelected) {
            pn.setColors(list.getSelectionForeground(), list
                    .getSelectionBackground());
        } else {
            pn.setColors(list.getSelectionBackground(), list
                    .getSelectionForeground());
        }
        v.setSelected(isSelected);

        return pn;
    }

    private final static int preferredLength = 80;
    private final static Dimension dim = new Dimension(preferredLength,
            preferredLength);

    private class IconLoader implements Runnable {
        private ImageListPanel pn;
        private String path;
        private JList c;
        //private int index;

        IconLoader(final ImageListPanel pn, final String path, final JList c,
                final int index) {
            synchronized (panels) {
                panels.put(path, new SoftReference<ImageListPanel>(pn));
            }
            this.pn = pn;
            this.path = path;
            this.c = c;
            //this.index = index;
            pn.setIconPreferredSize(dim);
        }

        /*
         * @see java.lang.Runnable#run()
         */
        public void run() {
            try {
                availThreads.acquire();
                try {
                    Icon icon;
                    final java.io.File file = new java.io.File(path);
                    FileInputStream fis = new FileInputStream(file);
                    icon = new ImageIcon(GraphicsUtilities.createThumbnail(
                            ImageIO.read(fis), 80));
                    fis.close();
                    fis = null;
                    pn.setIcon(icon);
                    c.validate();
                    // c.doLayout();
                    c.repaint();

                } catch (final Exception e) {
                    bt747.sys.Generic.debug("Icon creation", e);
                }
                availThreads.release();
            } catch (final Exception e) {

            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.bt747.j2se.app.list.WaypointListCellComponent#getText()
     */
    public final String getText(final Object wp) {
        final String path = ((FileWaypoint) wp).getPath();
        int li = 0;
        int n;
        n = path.lastIndexOf('/');
        if (li < n + 1) {
            li = n + 1;
        }
        n = path.lastIndexOf('\\');
        if (li < n + 1) {
            li = n + 1;
        }
        return path.substring(li);
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.bt747.j2se.app.list.WaypointListCellComponent#getTooltip()
     */
    public final String getTooltip(final Object wp) {
        final FileWaypoint w = (FileWaypoint) wp;
        return "<html>" + CommonOut.getHtml(w.getGpsRecord());
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sf.bt747.j2se.app.list.WaypointListCellComponent#isRendererOf(java.lang.Object)
     */
    public final boolean isRendererOf(final Object wp) {
        return wp instanceof FileWaypoint;
    }

}
