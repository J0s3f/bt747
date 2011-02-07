/**
 * 
 */
package net.sf.bt747.j2se.app.utils.test;

import junit.framework.TestCase;
import net.sf.bt747.j2se.app.utils.ExternalTool;
import net.sf.bt747.test.TestUtils;

import bt747.sys.JavaLibBridge;
import bt747.sys.interfaces.BT747Hashtable;


/**
 * @author Mario
 *
 */
public class ExternalToolTest extends TestCase {

    static {
        TestUtils.setupEnvironment();
    }

    /**
     * A simple execution of a command - no replacement requested.
     * 
     * @throws Exception
     */
    public void testEchoSimple() throws Exception {
        ExternalTool et = new ExternalTool("echo abcd");
        BT747Hashtable tokens = JavaLibBridge.getHashtableInstance(5);
        String expectedResult = "abcd\n";
        byte[] result;
        result = et.execTool(tokens);
        String sResult = new String(result);
        sResult=sResult.replace("\r\n", "\n");
        assertEquals(expectedResult, sResult);
    }
    
    /**
     * Execution of a command - with replacement.
     * 
     * @throws Exception
     */
    public void testEchoReplace() throws Exception {
        ExternalTool et = new ExternalTool("echo ab%fcd");
        BT747Hashtable tokens = JavaLibBridge.getHashtableInstance(5);
        tokens.put("f","afilename");
        String expectedResult = "abafilenamecd\n";
        byte[] result;
        result = et.execTool(tokens);
        String sResult = new String(result);
        sResult=sResult.replace("\r\n", "\n");
  
        assertEquals(expectedResult,sResult);
    }
}
