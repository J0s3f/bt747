package org.snipecode.reg.test;

import org.snipecode.reg.RegUtil;

import junit.framework.TestCase;

public class RegUtilTestCase extends TestCase
{
    public static void testWriteRead()
    {
        // Create a key
        int handle = RegUtil.RegCreateKeyEx(RegUtil.HKEY_LOCAL_MACHINE, "SOFTWARE\\Java\\regutil")[RegUtil.NATIVE_HANDLE];
        
        //close the handle
        RegUtil.RegCloseKey(handle);

        //open a new handle with all access
        handle = RegUtil.RegOpenKey(RegUtil.HKEY_LOCAL_MACHINE, "SOFTWARE\\Java\\regutil",RegUtil.KEY_ALL_ACCESS)[RegUtil.NATIVE_HANDLE];
        
        //Write a value
        RegUtil.RegSetValueEx(handle, "TestName", "TestValue");
        //Read the value
        byte[] val = RegUtil.RegQueryValueEx(handle, "TestName");
        //Check the value
        System.out.println(new String(val).toString().trim());
        // Close the handle
        //delete the value
        RegUtil.RegDeleteKey(RegUtil.HKEY_LOCAL_MACHINE, "SOFTWARE\\SnipCode\\regutil");
        RegUtil.RegDeleteKey(RegUtil.HKEY_LOCAL_MACHINE, "SOFTWARE\\SnipCode");
    }
    
    public static void testReadEnum()
    {


        // Open a Handle
        int handle = RegUtil.RegOpenKey(RegUtil.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion", RegUtil.KEY_QUERY_VALUE)[RegUtil.NATIVE_HANDLE];
        
        // get the Number of Values in the key
        int[] info = RegUtil.RegQueryInfoKey(handle);
        int count = info[RegUtil.VALUES_NUMBER];
        int maxlen = info[RegUtil.MAX_VALUE_NAME_LENGTH];
        
        for(int index =0 ;index< count;index++)
        {
            // get the Name of a key
            // Note to use 1 greater than the length returned by query
            byte[] name = RegUtil.RegEnumValue(handle,index, maxlen+1);
            System.out.print(new String(name).trim() +" = ");
            
            // Get its Value
            byte[] values = RegUtil.RegQueryValueEx(handle, name);
            if(null!=values)
            System.out.print(new String(values).trim());
            System.out.println();
        }
        
        // Finally Close the handle
        RegUtil.RegCloseKey(handle);

    
    }
}

