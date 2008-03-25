/*
 * Created on 28 nov. 2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package bt747.ui;

import waba.fx.Image;

/**
 * @author Mario De Weerd
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Button extends waba.ui.Button {

    /**
     * @param text
     */
    public Button(String text) {
        super(text);
    }

    /**
     * @param img
     */
    public Button(Image img) {
        super(img);
    }
    public void repaint()
    {
        repaintNow();
    }
    
    

}
