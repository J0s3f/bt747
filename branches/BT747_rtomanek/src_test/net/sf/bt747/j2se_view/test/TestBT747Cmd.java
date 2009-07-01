/**
 * 
 */
package net.sf.bt747.j2se_view.test;

import gps.connection.GPSrxtx;
import net.sf.bt747.test.IBlue747Model;
import net.sf.bt747.test.TestModelConnect;

import bt747.j2se_view.BT747cmd;

/**
 * @author Mario
 * 
 */
public class TestBT747Cmd extends TestModelConnect {

    public static final String TEST_MTK14_FILE = "files/MTK14.EPO";

    public static String getResourcePath(String rsc) {
        return TestBT747Cmd.class.getResource(rsc).getPath(); // getClass().getResource("test1.csv")
    }

    public static void main(String[] args) {
        IBlue747Model.main(new String[0]);
        GPSrxtx.setDefaultGpsPortInstance(appPort);

        String[] myargs = { "-agps-clear", "-agps-url",
                "file://" + getResourcePath(TEST_MTK14_FILE) };
        BT747cmd.main(myargs);
    }
}
