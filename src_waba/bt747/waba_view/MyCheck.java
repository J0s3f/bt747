/**
 * 
 */
package bt747.waba_view;

import waba.ui.Check;

/**
 * @author Mario De Weerd
 * 
 */
public final class MyCheck extends Check {

    /**
     * @param text
     */
    public MyCheck(final String text) {
        super(text);
    }

    // @Override
    public final int getPreferredHeight() {
        return fm.height - 4;
    }

    // @Override
    public final int getPreferredWidth() {
        return super.getPreferredWidth() + getPreferredHeight()
                - super.getPreferredHeight() - 1;
    }

}
