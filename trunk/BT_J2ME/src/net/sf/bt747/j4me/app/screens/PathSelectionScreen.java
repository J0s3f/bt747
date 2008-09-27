package net.sf.bt747.j4me.app.screens;

import java.util.Enumeration;


import org.j4me.ui.DeviceScreen;
import org.j4me.ui.Menu;
import org.j4me.ui.MenuItem;
import org.j4me.ui.components.MenuOption;

/**
 * Let the user select a path - file or directory depending on the parameters.
 * One directory is shown at a time. Kind of extension to J4ME.
 * 
 * @author Mario De Weerd
 * 
 */
public class PathSelectionScreen extends Menu {

    private FileUsage fileUsage = new FileManager();
    private boolean isGetDir;

    public PathSelectionScreen(String title, DeviceScreen previous,
            String path, boolean dir) {
        super("Select path", previous);
        fileUsage = new FileManager();
        currentPath = path;
        isGetDir = dir;
    }

    private String currentPath;

    private void appendDirItem(final String path) {
        final String p = path;
        appendMenuOption(new MenuItem() {
            public String getText() {
                return p;
            }

            public void onSelection() {
                appendPath(p);
            }
        });
    }

    private void appendFileItem(final String path) {
        final String p = path;
        appendMenuOption(new MenuItem() {
            public String getText() {
                return p;
            }

            public void onSelection() {
                pathSelected();
            }
        });
    }

    private void addUpItem() {
        appendMenuOption(new MenuItem() {
            public String getText() {
                return "[..]";
            }

            public void onSelection() {
                removePath();
            }
        });
    }

    private void addCurrentItem() {
        appendMenuOption(new MenuItem() {
            public String getText() {
                return "[Current dir]";
            }

            public void onSelection() {
                pathSelected();
            }
        });
    }

    protected void appendPath(final String p) {
        currentPath += "/" + p;
        if (currentPath.endsWith("/")) {
            currentPath = currentPath.substring(0, currentPath.length() - 1);
        }
        this.show();
    }

    protected void removePath() {
        int index;
        // Log.info("Up from " + currentPath);
        index = currentPath.lastIndexOf('/');
        if (index > 0) {
            currentPath = currentPath
                    .substring(0, currentPath.lastIndexOf('/'));
        } else {
            currentPath = "";
        }
        // Log.info("Up to " + currentPath);
        this.show();
    }

    public void show() {
        setup();
        super.show();
    }

    // static final String fs = System.getProperty("file.separator");

    private void setup() {
        this.deleteAll();
        this.setTitle(currentPath);
        // // Log.info(System.getProperty("file.separator"));
        // Log.info(currentPath);
        // Log.info("Before enum");
        if (currentPath.length() >= 1) {
            if (isGetDir) {
                addCurrentItem();
                // Log.info("Add current");
            }
            // Log.info("Add up from " + currentPath);
            addUpItem();

            try {
                String url;
                url = "file://" + currentPath + "/";
                // Log.info("Get dir " + url);
                Enumeration files = fileUsage.getFiles(url);
                while (files.hasMoreElements()) {
                    String p = (String) files.nextElement();
                    // Log.info("Add " + p);
                    if (p.endsWith("/")) {
                        appendDirItem(p);
                    } else if (!isGetDir) {
                        appendFileItem(p);
                    }
                }
                files = null;
            } catch (Exception e) {
                // TODO: handle exception
                // Log.error(currentPath, e);
            }
        } else {
            try {
                Enumeration roots = fileUsage.listRoots();
                while (roots.hasMoreElements()) {
                    String p = (String) roots.nextElement();
                    // Log.info("Add " + p);
                    appendDirItem(p);
                }
            } catch (Exception e) {
                // TODO: handle exception
                // Log.error(currentPath, e);
            }
        }
        try {
            fileUsage.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
        // Log.info("End ");

    }

    protected void acceptNotify() {
        ((MenuOption) get(getSelected())).select();
    }

    private void pathSelected() {
        MenuOption s = (MenuOption) get(getSelected());
        String p = s.getLabel();
        if (isGetDir && p.endsWith("/")) {
            // Selection done
            currentPath += "/" + p.substring(1, p.length() - 1);
            super.acceptNotify();
        } else if (!isGetDir && !p.startsWith("[") && !p.endsWith("/")) {
            // Valid path
            super.acceptNotify();
        }
        notifyPathSelected(currentPath);
        previous.show();
    }

    protected void notifyPathSelected(final String path) {

    }

    public String getPath() {
        return currentPath;
    }

}
