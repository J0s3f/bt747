/**
 * 
 */
package net.sourceforge.bt747.bt747.waba_view;

import waba.ui.Check;

/**
 * @author Mario De Weerd
 * 
 */
public final class MyCheck extends Check {

    /**
     * @param text
     */
    public MyCheck(String text) {
        super(text);
    }

    // @Override
    final public int getPreferredHeight() {
        return fm.height - 4;
    }

    // @Override
    final public int getPreferredWidth() {
        return super.getPreferredWidth() + getPreferredHeight()
                - super.getPreferredHeight() - 1;
    }

}
