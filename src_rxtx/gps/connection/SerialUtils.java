package gps.connection;

import java.util.Vector;

import org.snipecode.reg.RegUtil;

public class SerialUtils {

	private static final String[] knownPorts = { "\\device\\usbser",
			"\\device\\slabser", "\\device\\profilicserial",
			"\\device\\silabser" };

	public final static String SERIALPATH = "HARDWARE\\DEVICEMAP\\SERIALCOMM\\";

	public final static String[] getSerialPorts() {
		int handle = RegUtil.RegOpenKey(RegUtil.HKEY_LOCAL_MACHINE, SERIALPATH,
				RegUtil.KEY_QUERY_VALUE)[RegUtil.NATIVE_HANDLE];
		final Vector<String> portList = new Vector<String>();

		// get the Number of Values in the key
		int[] info = RegUtil.RegQueryInfoKey(handle);
		int count = info[RegUtil.VALUES_NUMBER];
		int maxlen = info[RegUtil.MAX_VALUE_NAME_LENGTH];
		for (int index = 0; index < count; index++) {
			// get the Name of a key
			// Note to use 1 greater than the length returned by query
			byte[] name = RegUtil.RegEnumValue(handle, index, maxlen + 1);

			// System.out.print(new String(name).trim() +" = ");

			// Get its Value
			byte[] values = RegUtil.RegQueryValueEx(handle, name);
			if (null != values) {
				final String n = (new String(name)).trim().toLowerCase();
				for (int j = 0; j < knownPorts.length; j++) {
					if (n.startsWith(knownPorts[j])) {
						portList.add(new String(values).trim());
						break;
					}
				}
			}
		}
		// Finally Close the handle
		RegUtil.RegCloseKey(handle);

		return portList.toArray(new String[portList.size()]);
	}
}
