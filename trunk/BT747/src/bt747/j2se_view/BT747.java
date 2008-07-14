package bt747.j2se_view;

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
//***  The application was written using the SuperWaba toolset.    ***
//***  This is a proprietary development environment based in      ***
//***  part on the Waba development environment developed by       ***                                   
//***  WabaSoft, Inc.                                              ***
//********************************************************************                              
/**
 * Main class (application entry) An empty wrapper to maintain compatibility
 * after moving classes into specific package.
 * 
 * @author Mario De Weerd
 */
public class BT747 extends bt747.waba_view.AppBT747 {
    
    BT747_Main j2se_view;
    
    @Override
    public void onStart() {
        super.onStart();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                BT747_Main.myLookAndFeel();
                j2se_view= new BT747_Main(m,c);
                j2se_view.setVisible(true);
            }
        });
    }

}
