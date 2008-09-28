//********************************************************************
//***                           BT 747                             ***
//***                      April 14, 2007                          ***
//***                  (c)2007 Mario De Weerd                      ***
//***                     m.deweerd@ieee.org                       ***
//***  **********************************************************  ***
//***  Software is provided "AS IS," without a warranty of any     ***
//***  kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,***
//***  INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS  ***
//***  FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY    ***
//***  EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE ***
//***  IS ASSUMED BY THE USER. See the GNU General Public License  ***
//***  for more details.                                           ***
//***  *********************************************************** ***
package bt747.j2se_view;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Implements a messageBox.
 * 
 * @author Mario De Weerd
 */
public class MessageBox extends javax.swing.JDialog implements ActionListener {

    /**
     * 
     */
    private static final long serialVersionUID = -5854970044555876452L;

    /**
     * @param title
     * @param msg
     */
    public MessageBox(final String title, final String msg) {
        this(title, msg, null, 2, 2);
    }

    /**
     * Constructor.
     * 
     * @param title
     * @param text
     * @param buttonCaptions
     */
    public MessageBox(final String title, final String msg,
            final String[] buttonCaptions) {
        this(title, msg, buttonCaptions, 2, 2);
    }

    /**
     * Constructor.
     * 
     * @param title
     * @param text
     * @param buttonCaptions
     * @param gap
     * @param insideGap
     */
    public MessageBox(final String title, final String msg,
            final String[] buttonCaptions, final int gap, final int insideGap) {
        super((JFrame) null, title);
        // myPanel = new JPanel(new BoxLayout(null, BoxLayout.PAGE_AXIS));

        Box myBox;
        Box myButtonBox;
        String[] myButtonCaptions;
        if (buttonCaptions == null) {
            myButtonCaptions = new String[0];
        } else {
            myButtonCaptions = buttonCaptions;
        }
        myBox = Box.createVerticalBox();
        myButtonBox = Box.createHorizontalBox();
        myButtonBox.setAlignmentX(Box.CENTER_ALIGNMENT);

        // myPanel=new JPanel(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
        getContentPane().setLayout(new GridLayout(2, 1));
        getContentPane().add(myBox);
        // myPanel.add(myBox);
        myBox.add(new JLabel("<html><p align=center>"
                + msg.replaceAll("\\|", "<br>") + "</html>", JLabel.CENTER));
        yesButton = new JButton(
                (myButtonCaptions.length >= 1) ? myButtonCaptions[0] : "Yes");
        yesButton.addActionListener(this);
        myButtonBox.add(yesButton);
        if (myButtonCaptions.length == 0 || buttonCaptions.length >= 2) {
            noButton = new JButton(
                    (myButtonCaptions.length >= 2) ? myButtonCaptions[1] : "No");
            noButton.addActionListener(this);
            myButtonBox.add(noButton);
        }
        getContentPane().add(myButtonBox);
        pack();
    }

    public MessageBox(final boolean n, final String title, final String Text,
            final String[] Buttons) {
        this(title, Text, Buttons);
    }

    /**
     * When true the application that set up the MessageBox is waiting for it to
     * get an answer. This is also the object that is used to synchronize and
     * must be accessed through the getter and setter.
     */
    private Boolean WaitForAnswer = Boolean.valueOf(false);

    public final void popupBlockingModal() {
        setModal(true);
        setWaitForAnswer(true);
        setVisible(true);
        toFront();
        while (getWaitForAnswer()) {
            try {
                wait();
            } catch (Exception e) {
                setWaitForAnswer(false);
            }
        }
    }

    public final void popupModal() {
        setModal(false);
        setVisible(true);
        toFront();
    }

    /**
     * @return Returns the popped.
     */
    public final boolean isPopped() {
        return isVisible();
    }

    public final void unpop() {
        setVisible(false);
        dispose();
    }

    public final int getPressedButtonIndex() {
        return answer;
    }

    private JButton yesButton = null;
    private JButton noButton = null;
    private int answer = -1;

    public final int getAnswer() {
        return answer;
    }

    public final void actionPerformed(final ActionEvent e) {
        if (yesButton == e.getSource()) {
            // System.err.println("User chose yes.");
            answer = 0;
            setVisible(false);
            setWaitForAnswer(false);
        } else if (noButton == e.getSource()) {
            // System.err.println("User chose no.");
            answer = 1;
            setVisible(false);
            setWaitForAnswer(false);
        }
    }

    public final synchronized boolean getWaitForAnswer() {
        return WaitForAnswer.booleanValue();
    }

    public final synchronized void setWaitForAnswer(final boolean waitForAnswer) {
        WaitForAnswer = Boolean.valueOf(waitForAnswer);
    }
}
