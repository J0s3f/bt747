/**
 * 
 */
package bt747.waba_view;

import waba.ui.Check;

/**
 * @author Mario De Weerd
 *
 */
public class MyCheck extends Check {

    /**
     * @param text
     */
    public MyCheck(String text) {
        super(text);
    }
    
    //@Override
    public int getPreferredHeight() {
        return fm.height-4;
    }
    
    //@Override
    public int getPreferredWidth() {
        return super.getPreferredWidth()+getPreferredHeight()-super.getPreferredHeight()-1;
    }

}
