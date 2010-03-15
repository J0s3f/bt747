/*******************************************************************************
 * Copyright (c) 2010 Mario De Weerd.
 * 
 * Project BT747
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.html
 * 
 * Software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. THE ENTIRE RISK ARISING OUT OF USING THE SOFTWARE
 * IS ASSUMED BY THE USER.
 * See the GNU General Public License Version 3 for details.
 * 
 * Contributors:
 *     Mario De Weerd - main contributor
 ******************************************************************************/
package gps.connection;

import java.io.File;

/**
 * Determine which native library to use and set java.library.path
 * 
 * Some OS information take from <a
 * href="http://lopica.sourceforge.net/os.html">
 * http://lopica.sourceforge.net/os.html</a> .
 * 
 * @author mdeweerd
 * 
 */
public class RXTXLibPathSetting {

	private static final int OS_LINUX = 1;
	private static final int OS_WINDOWS = 2;
	private static final int OS_MAC = 3;
	private static final int OS_SOLARIS = 4;

	private static final String fs = File.separator; // System.getProperty("file.separator");
	private static final String WIN_PATH = "Windows" + fs;
	private static final String WIN32SUBPATH = "win32" + fs;
	private static final String WIN32SUBPATH2 = "i368-mingw32" + fs;
	private static final String WIN64SUBPATH = "win64" + fs;
	private static final String WINDLLBASENAME = "rxtxSerial.dll";

	private static final String LINUX_PATH = "sparc-solaris";
	private static final String LINUX_ARM_SUBPATH = "arm-xscale-linux-gnu";
	private static final String LINUX_I686_SUBPATH = "i686-unknown-linux-gnu";
	private static final String LINUX_IA64_SUBPATH = "ia64-unknown-linux-gnu";
	private static final String LINUX_X86_64_SUBPATH = "x86_64-unknown-linux-gnu";
	private static final String LINUX_I686_PC_SUBPATH = "i686-pc-linux-gnu";
	private static final String LINUXSOBASENAME = "librxtxSerial.so";

	
	private static final String SOL_PATH = "sparc-solaris";
	private static final String SOL32SUBPATH = "sparc32-sun-solaris2.8";
	private static final String SOL64SUBPATH = "sparc64-sun-solaris2.8";
	private static final String SOLSOBASENAME = "librxtxSerial.so";
	
	private static final String MAC_PATH = "Mac_OS_X" + fs;
	private static final String MAC_10_5_SUBPATH = "mac-10.5" + fs;
	private static final String MACLIB = "librxtxSerial.jnilib";


	private static final String RXTX_2_1_7_PATH = "rxtx-2.1-7-bins-r2" + fs;
	private static final String RXTX_2_2_PREPATH = "rxtx-2.2pre2-bins" + fs;

	public static final String getDllPath() {
		final String OsName = System.getProperty("os.name");
		final String OsArch = System.getProperty("os.arch");
		final String OsVersion = System.getProperty("os.version");
		final String JavaVersion = System.getProperty("java.version");
		final String dataModel = System.getProperty("sun.arch.data.model");

		/* Determine os type */
		int osType = 0;
		if (OsName.startsWith("Mac")) {
			osType = OS_MAC;
		} else if (OsName.startsWith("Linux")) {
			osType = OS_LINUX;
		} else if (OsName.startsWith("Windows")) {
			osType = OS_WINDOWS;
		} else if (OsName.startsWith("SunOS")) {
			osType = OS_SOLARIS;
		}

		String path = "";
		switch (osType) {
		case OS_WINDOWS:
			path += WIN_PATH;
			/*
			 * On Windows, processor type is important. Sometimes system too
			 * (uses os version).
			 */
			if (dataModel.equals("64")) {
				// 64 bit version
				path = RXTX_2_2_PREPATH + path + WIN64SUBPATH;
			} else {
				// 32 bit version
				path = RXTX_2_1_7_PATH + path + WIN32SUBPATH2;
			}
			path += WINDLLBASENAME;
			break;
		case OS_MAC: {
			float osVersion = Float.valueOf(OsVersion);
			if(osVersion < 10.45f) {
				path = RXTX_2_1_7_PATH + MAC_PATH + MACLIB;
			} else {
				path = RXTX_2_2_PREPATH + MAC_PATH + MAC_10_5_SUBPATH + MACLIB;
			}
		}
		case OS_LINUX: {
			
		}
		default:
			break;
		}

		// System.load(filename);
		return path;
	}

	/**
	 * Load the RXTX library based on system identification.
	 * 
	 * @param basePath
	 *            Root path where to look for DLL ('lib' directory). This path
	 *            will be prefixed to the relative DLL or SO path and fed into
	 *            System.load.
	 */
	public static void loadRxTxLibrary(final String basePath) {
		String dllPath = getDllPath();
		System.load(basePath + dllPath);
	}

	/**
	 * Local main used for interactive testing.
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		loadRxTxLibrary(ClassLoader.getSystemResource(".").getPath() + ".."
				+ fs + ".." + fs + "lib" + fs);
	}
}
